/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.arrow.driver.jdbc;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.ConsoleReporter;
import org.apache.arrow.driver.jdbc.authentication.UserPasswordAuthentication;
import org.apache.arrow.driver.jdbc.utils.ArrowFlightConnectionConfigImpl.ArrowFlightConnectionProperty;
import org.apache.arrow.driver.jdbc.utils.MockFlightSqlProducer;
import org.apache.arrow.memory.BufferAllocator;
import org.apache.arrow.memory.RootAllocator;
import org.apache.arrow.util.AutoCloseables;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.apache.arrow.flight.FlightStream.metrics;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ArrowFlightJdbcDriver}.
 */
public class ArrowFlightJdbcDriverTest {

  @ClassRule
  public static final FlightServerTestRule FLIGHT_SERVER_TEST_RULE;
  private static final MockFlightSqlProducer PRODUCER = new MockFlightSqlProducer();

  static {
    UserPasswordAuthentication authentication =
        new UserPasswordAuthentication.Builder().user("user1", "pass1").user("user2", "pass2")
            .build();

    FLIGHT_SERVER_TEST_RULE = new FlightServerTestRule.Builder()
        .authentication(authentication)
        .producer(PRODUCER)
        .build();
  }

  private BufferAllocator allocator;
  private ArrowFlightJdbcConnectionPoolDataSource dataSource;

  @Before
  public void setUp() throws Exception {
    allocator = new RootAllocator(Long.MAX_VALUE);
    dataSource = FLIGHT_SERVER_TEST_RULE.createConnectionPoolDataSource();
  }

  @After
  public void tearDown() throws Exception {
    Collection<BufferAllocator> childAllocators = allocator.getChildAllocators();
    AutoCloseables.close(childAllocators.toArray(new AutoCloseable[0]));
    AutoCloseables.close(dataSource, allocator);
  }

  /**
   * Tests whether the {@link ArrowFlightJdbcDriver} is registered in the
   * {@link DriverManager}.
   *
   * @throws SQLException If an error occurs. (This is not supposed to happen.)
   */
  @Test
  public void testDriverIsRegisteredInDriverManager() throws Exception {
    assertTrue(DriverManager.getDriver("jdbc:arrow-flight://localhost:32010") instanceof
        ArrowFlightJdbcDriver);
    assertTrue(DriverManager.getDriver("jdbc:arrow-flight-sql://localhost:32010") instanceof
        ArrowFlightJdbcDriver);
  }

  /**
   * Tests whether the {@link ArrowFlightJdbcDriver} returns null when provided with an
   * unsupported URL prefix.
   */
  @Test
  public void testShouldDeclineUrlWithUnsupportedPrefix() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();

    assertNull(driver.connect("jdbc:mysql://localhost:32010",
        dataSource.getProperties("flight", "flight123")));
  }

  /**
   * Tests whether the {@link ArrowFlightJdbcDriver} can establish a successful
   * connection to the Arrow Flight client.
   *
   * @throws Exception If the connection fails to be established.
   */
  @Test
  public void testShouldConnectWhenProvidedWithValidUrl() throws Exception {
    // Get the Arrow Flight JDBC driver by providing a URL with a valid prefix.
    final Driver driver = new ArrowFlightJdbcDriver();

    try (Connection connection =
             driver.connect("jdbc:arrow-flight://" +
                     dataSource.getConfig().getHost() + ":" +
                     dataSource.getConfig().getPort() + "?" +
                     "useEncryption=false",
                 dataSource.getProperties(dataSource.getConfig().getUser(), dataSource.getConfig().getPassword()))) {
      assertTrue(connection.isValid(300));
    }
    try (Connection connection =
             driver.connect("jdbc:arrow-flight-sql://" +
                     dataSource.getConfig().getHost() + ":" +
                     dataSource.getConfig().getPort() + "?" +
                     "useEncryption=false",
                 dataSource.getProperties(dataSource.getConfig().getUser(), dataSource.getConfig().getPassword()))) {
      assertTrue(connection.isValid(300));
    }
  }

  @Test
  public void testQueryParameters() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table person (id int, name varchar, primary key(id))");
      } catch (Exception ignored) {}
      try(PreparedStatement ps = con.prepareStatement("select * from person where id=$1")) {
        ParameterMetaData md = ps.getParameterMetaData();
        assertEquals(1, md.getParameterCount());
        assertEquals("Int", md.getParameterTypeName(1));
        ps.setInt(1, 1);
        ResultSet rs = ps.executeQuery();
        assertFalse(rs.next()); // should be no records
      }
    }
  }

  @Test
  public void testUpdateNotPrepared() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.executeUpdate("create table person (id int, name varchar, primary key(id))");
        stmt.executeUpdate("insert into person (id, name) values (1, 'Brent')");
      } catch (Exception ignored) {}
    }
  }

  @Test
  public void testQueryNotPrepared() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.executeQuery("select 1;");
      } catch (Exception ignored) {}
    }
  }

  @Test
  public void testUpdatePreparedNoParams() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.executeUpdate("create table person (id int, name varchar, primary key(id))");
      } catch (Exception ignored) {}
      String sql = "insert into person values (1, 'Brent')";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.executeUpdate();
      }
    }
  }

  @Test
  public void testUpdatePreparedAsQuery() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    String sql = "create table person (id int, name varchar, primary key(id))";
    try (Connection con = driver.connect(conString, props); PreparedStatement stmt = con.prepareStatement(sql)) {
      try {
        stmt.executeQuery();
      } catch (Exception ignored) {}
    }
  }

  @Test
  public void testUpdatePreparedParams() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.executeUpdate("create table person (id int, name varchar, primary key(id))");
      } catch (Exception ignored) {}
      String sql = "insert into person values ($1, $2)";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.getParameterMetaData();
        ps.setInt(1, 1);
        ps.setString(2, "Brent");
        ps.executeUpdate();
      }
    }
  }

  @Test
  public void testQueryPreparedNoParams() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props)) {
      String sql = "select 1;";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.executeQuery();
      }
    }
  }

  @Test
  public void testQueryPreparedParams() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.executeUpdate("create table person (id int, name varchar, primary key(id))");
      } catch (Exception ignored) {}
      String sql = "select * from person where id=$1;";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.getParameterMetaData();
        ps.setInt(1, 1);
        ps.executeQuery();
      }
    }
  }

  @Test
  public void testWarehouse() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table warehouse (\n" +
                "w_id int,\n" +
                "w_name string,\n" +
                "w_street_1 string,\n" +
                "w_street_2 string,\n" +
                "w_city string,\n" +
                "w_state string,\n" +
                "w_zip string,\n" +
                "w_tax float,\n" +
                "w_ytd float,\n" +
                "primary key (w_id)\n" +
                ");\n");
      } catch (Exception ignored) {}
      String sql = "SELECT w_street_1, w_street_2, w_city, w_state, w_zip, w_name FROM warehouse WHERE w_id = $1";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ParameterMetaData md = ps.getParameterMetaData();
        assertEquals(1, md.getParameterCount());
        assertEquals("Int", md.getParameterTypeName(1));
        ps.setInt(1, 1);
        ResultSet rs = ps.executeQuery();
        assertNotNull(rs);
      }
    }
  }

  @Test
  public void testDistrict() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table district (\n" +
                "d_id int,\n" +
                "d_w_id int,\n" +
                "d_name string,\n" +
                "d_street_1 string,\n" +
                "d_street_2 string,\n" +
                "d_city string,\n" +
                "d_state string,\n" +
                "d_zip string,\n" +
                "d_tax float,\n" +
                "d_ytd float,\n" +
                "d_next_o_id int,\n" +
                "primary key (d_w_id, d_id)\n" +
                ");\n");
      } catch (Exception ignored) {}
      String sql = "UPDATE district SET d_next_o_id = $1 + 1 WHERE d_id = $2 AND d_w_id = $3";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setLong(1, 0);
        ps.setInt(2, 2);
        ps.setInt(3, 1);
        ps.executeUpdate();
      }
    }
  }

  @Test
  public void testDistrictForUpdate() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table district (\n" +
                "d_id int,\n" +
                "d_w_id int,\n" +
                "d_name string,\n" +
                "d_street_1 string,\n" +
                "d_street_2 string,\n" +
                "d_city string,\n" +
                "d_state string,\n" +
                "d_zip string,\n" +
                "d_tax float,\n" +
                "d_ytd float,\n" +
                "d_next_o_id int,\n" +
                "primary key (d_w_id, d_id)\n" +
                ");\n");
      } catch (Exception ignored) {}
      String sql = "SELECT d_next_o_id, d_tax FROM district WHERE d_id = $1 AND d_w_id = $2 FOR UPDATE";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, 0);
        ps.setInt(2, 2);
        ps.executeUpdate();
      }
    }
  }

  @Test
  public void testHistory() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table history (\n" +
                "h_c_id int,\n" +
                "h_date timestamp,\n" +
                "h_amount float,\n" +
                "h_data varchar,\n" +
                "PRIMARY KEY(h_c_id)\n" +
                ");");
      } catch (Exception ignored) {}
      String sql = "INSERT INTO history(h_c_id, h_date, h_amount, h_data) VALUES($1, $2, $3, $4)";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, 1);
        ps.setString(2, "2023-02-03 12:31:00.16");
        ps.setFloat(3, 1.0f);
        ps.setString(4, "test");
        ResultSet rs = ps.executeQuery();
        assertNotNull(rs);
      }
    }
  }

  @Test
  public void testRegion() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table region (r_regionkey int, r_name varchar, r_comment varchar, primary key (r_regionkey))");
      } catch (Exception ignored) {}
      String sql = "insert into region (r_regionkey, r_name, r_comment) values\n" +
              "\t($1, $2, $3),\n" +
              "\t($4, $5, $6),\n" +
              "\t($7, $8, $9),\n" +
              "\t($10, $11, $12)";
      try(PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, 1);
        ps.setString(2, "Africa");
        ps.setString(3, "Africa");

        ps.setInt(4, 1);
        ps.setString(5, "Americas");
        ps.setString(6, "Americas");

        ps.setInt(7, 1);
        ps.setString(8, "Europe");
        ps.setString(9, "Europe");

        ps.setInt(10, 1);
        ps.setString(11, "Asia");
        ps.setString(12, "Asia");

        int res = ps.executeUpdate();
        assertNotNull(res);
      }
    }
  }

  @Test
  public void testOrders() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table orders (\n" +
                "o_id int, \n" +
                "o_d_id int, \n" +
                "o_w_id int,\n" +
                "o_c_id int,\n" +
                "o_entry_d timestamp,\n" +
                "o_carrier_id int,\n" +
                "o_ol_cnt int, \n" +
                "o_all_local int,\n" +
                "PRIMARY KEY(o_w_id, o_d_id, o_id) \n" +
                ")");
      } catch (Exception ignored) {}
      String sql = "INSERT INTO orders (o_id, o_d_id, o_w_id, o_c_id, o_entry_d, o_ol_cnt, o_all_local) VALUES($1, $2, $3, $4, $5, $6, $7)";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ps.setInt(3, 1);
        ps.setInt(4, 1);
        ps.setString(5, "2023-02-03 12:31:00.16");
        ps.setInt(6, 1);
        ps.setInt(7, 1);
        ps.executeUpdate();
      }
    }
  }

  @Test
  public void testSubqyeryParams() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table orders (\n" +
                "o_id int, \n" +
                "o_d_id int, \n" +
                "o_w_id int,\n" +
                "o_c_id int,\n" +
                "o_entry_d timestamp,\n" +
                "o_carrier_id int,\n" +
                "o_ol_cnt int, \n" +
                "o_all_local int,\n" +
                "PRIMARY KEY(o_w_id, o_d_id, o_id) \n" +
                ")");
      } catch (Exception ignored) {}
      String sql = "SELECT o_id, o_entry_d, COALESCE(o_carrier_id,0) FROM orders WHERE o_w_id = $1 AND o_d_id = $2 AND o_c_id = $3 AND o_id = (SELECT MAX(o_id) FROM orders WHERE o_w_id = $4 AND o_d_id = $5 AND o_c_id = $6)";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ps.setInt(3, 1);
        ps.setInt(4, 1);
        ps.setInt(5, 1);
        ps.setInt(6, 1);
        ps.executeQuery();
      }
    }
  }

  @Test
  public void testTpcCQuery6() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    String sql = "SELECT\n" +
            "    27 as s_quantity,\n" +
            "    's_data' as s_data,\n" +
            "    's_dist_01' as s_dist_01,\n" +
            "    's_dist_02' as s_dist_02,\n" +
            "    's_dist_03' as s_dist_03,\n" +
            "    's_dist_04' as s_dist_04,\n" +
            "    's_dist_05' as s_dist_05,\n" +
            "    's_dist_06' as s_dist_06,\n" +
            "    's_dist_07' as s_dist_07,\n" +
            "    's_dist_08' as s_dist_08,\n" +
            "    's_dist_09' as s_dist_09,\n" +
            "    's_dist_10' as s_dist_10\n" +
            ";\n";
    try (Connection con = driver.connect(conString, props)) {
      try(PreparedStatement stmt = con.prepareStatement(sql)) {
        long sum = 0;
        long cnt = 100;
        for(long i = 0; i < cnt; i++) {
//          stmt.setInt(1, 1);
//          stmt.setInt(2, 1);
          long start = System.currentTimeMillis();
          try(ResultSet rs = stmt.executeQuery()) {
            assertTrue(rs.next());
            int quantity = rs.getInt(1);
            System.out.format("quantity=%d\n", quantity);
            assertFalse(rs.next());
            long end = System.currentTimeMillis();
            long delta = end - start;
            sum += delta;
            System.out.format("Selected single row in %dms\n\n", delta);
          }
          Thread.sleep(100);
        }
        System.out.format("Average time=%dms\n", sum / cnt);
        ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.report();
      }
    }
  }

  @Test
  public void testCustomer() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table customer (\n" +
                "c_id int,\n" +
                "c_d_id int,\n" +
                "c_w_id int,\n" +
                "c_first string,\n" +
                "c_middle string,\n" +
                "c_last string,\n" +
                "c_street_1 string,\n" +
                "c_street_2 string,\n" +
                "c_city string,\n" +
                "c_state string,\n" +
                "c_zip string,\n" +
                "c_phone string,\n" +
                "c_since datetime,\n" +
                "c_credit string,\n" +
                "c_credit_lim int,\n" +
                "c_discount float,\n" +
                "c_balance float,\n" +
                "c_ytd_payment float,\n" +
                "c_payment_cnt int,\n" +
                "c_delivery_cnt int,\n" +
                "c_data string,\n" +
                "PRIMARY KEY(c_w_id, c_d_id, c_id)\n" +
                ");");
      } catch (Exception ignored) {}
      String sql = "SELECT count(c_id) FROM customer WHERE c_w_id = $1 AND c_d_id = $2 AND c_last = $3";
      try(PreparedStatement ps = con.prepareStatement(sql)) {
        ParameterMetaData md = ps.getParameterMetaData();
        ps.setInt(1, 1);
        ps.setInt(2, 1);
        ps.setString(3, "Acme");
        ResultSet rs = ps.executeQuery();
        assertNotNull(rs);
      }
    }
  }

  @Test
  public void testDmlParameters() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      try {
        stmt.execute("create table person (id int, name varchar, primary key(id))");
      } catch (Exception ignored) {}
      try(PreparedStatement ps = con.prepareStatement("insert into person (id, name) values ($1, $2)")) {
        ParameterMetaData md = ps.getParameterMetaData();
        assertEquals(2, md.getParameterCount());
        assertEquals("Int", md.getParameterTypeName(1));
        assertEquals("Utf8", md.getParameterTypeName(2));
        ps.setInt(1, 1);
        ps.setString(2, "Alan");
        assertEquals(-1, ps.executeUpdate());
        ResultSet rs = ps.getResultSet();
        assertNull(rs);
      }
    }
  }

  @Test
  public void testSetVariable() throws Exception {
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties props = new Properties();
    props.setProperty("user", "admin");
    props.setProperty("password", "password");
    props.setProperty("useEncryption", "false");
    String conString = "jdbc:arrow-flight://127.0.0.1:50060";
    try (Connection con = driver.connect(conString, props); Statement stmt = con.createStatement()) {
      assertTrue(stmt.execute("SET UNIQUE_CHECKS=0"));
    }
  }

  @Test
  public void testConnectWithInsensitiveCasePropertyKeys() throws Exception {
    // Get the Arrow Flight JDBC driver by providing a URL with insensitive case property keys.
    final Driver driver = new ArrowFlightJdbcDriver();

    try (Connection connection =
             driver.connect("jdbc:arrow-flight://" +
                     dataSource.getConfig().getHost() + ":" +
                     dataSource.getConfig().getPort() + "?" +
                     "UseEncryptiOn=false",
                 dataSource.getProperties(dataSource.getConfig().getUser(), dataSource.getConfig().getPassword()))) {
      assertTrue(connection.isValid(300));
    }
    try (Connection connection =
             driver.connect("jdbc:arrow-flight-sql://" +
                     dataSource.getConfig().getHost() + ":" +
                     dataSource.getConfig().getPort() + "?" +
                     "UseEncryptiOn=false",
                 dataSource.getProperties(dataSource.getConfig().getUser(), dataSource.getConfig().getPassword()))) {
      assertTrue(connection.isValid(300));
    }
  }

  @Test
  public void testConnectWithInsensitiveCasePropertyKeys2() throws Exception {
    // Get the Arrow Flight JDBC driver by providing a property object with insensitive case keys.
    final Driver driver = new ArrowFlightJdbcDriver();
    Properties properties =
        dataSource.getProperties(dataSource.getConfig().getUser(), dataSource.getConfig().getPassword());
    properties.put("UseEncryptiOn", "false");

    try (Connection connection =
             driver.connect("jdbc:arrow-flight://" +
                 dataSource.getConfig().getHost() + ":" +
                 dataSource.getConfig().getPort(), properties)) {
      assertTrue(connection.isValid(300));
    }
    try (Connection connection =
             driver.connect("jdbc:arrow-flight-sql://" +
                 dataSource.getConfig().getHost() + ":" +
                 dataSource.getConfig().getPort(), properties)) {
      assertTrue(connection.isValid(300));
    }
  }

  /**
   * Tests whether an exception is thrown upon attempting to connect to a
   * malformed URI.
   */
  @Test(expected = SQLException.class)
  public void testShouldThrowExceptionWhenAttemptingToConnectToMalformedUrl() throws SQLException {
    final Driver driver = new ArrowFlightJdbcDriver();
    final String malformedUri = "yes:??/chainsaw.i=T333";

    driver.connect(malformedUri, dataSource.getProperties("flight", "flight123"));
  }

  /**
   * Tests whether an exception is thrown upon attempting to connect to a
   * malformed URI.
   *
   * @throws Exception If an error occurs.
   */
  @Test(expected = SQLException.class)
  public void testShouldThrowExceptionWhenAttemptingToConnectToUrlNoPrefix() throws SQLException {
    final Driver driver = new ArrowFlightJdbcDriver();
    final String malformedUri = "localhost:32010";

    driver.connect(malformedUri, dataSource.getProperties(dataSource.getConfig().getUser(),
        dataSource.getConfig().getPassword()));
  }

  /**
   * Tests whether an exception is thrown upon attempting to connect to a
   * malformed URI.
   */
  @Test
  public void testShouldThrowExceptionWhenAttemptingToConnectToUrlNoPort() {
    final Driver driver = new ArrowFlightJdbcDriver();
    SQLException e = assertThrows(SQLException.class, () -> {
      Properties properties = dataSource.getProperties(dataSource.getConfig().getUser(),
          dataSource.getConfig().getPassword());
      Connection conn = driver.connect("jdbc:arrow-flight://localhost", properties);
      conn.close();
    });
    assertTrue(e.getMessage().contains("URL must have a port"));
    e = assertThrows(SQLException.class, () -> {
      Properties properties = dataSource.getProperties(dataSource.getConfig().getUser(),
          dataSource.getConfig().getPassword());
      Connection conn = driver.connect("jdbc:arrow-flight-sql://localhost", properties);
      conn.close();
    });
    assertTrue(e.getMessage().contains("URL must have a port"));
  }

  /**
   * Tests whether an exception is thrown upon attempting to connect to a
   * malformed URI.
   */
  @Test
  public void testShouldThrowExceptionWhenAttemptingToConnectToUrlNoHost() {
    final Driver driver = new ArrowFlightJdbcDriver();
    SQLException e = assertThrows(SQLException.class, () -> {
      Properties properties = dataSource.getProperties(dataSource.getConfig().getUser(),
          dataSource.getConfig().getPassword());
      Connection conn = driver.connect("jdbc:arrow-flight://32010:localhost", properties);
      conn.close();
    });
    assertTrue(e.getMessage().contains("URL must have a host"));

    e = assertThrows(SQLException.class, () -> {
      Properties properties = dataSource.getProperties(dataSource.getConfig().getUser(),
          dataSource.getConfig().getPassword());
      Connection conn = driver.connect("jdbc:arrow-flight-sql://32010:localhost", properties);
      conn.close();
    });
    assertTrue(e.getMessage().contains("URL must have a host"));
  }

  /**
   * Tests whether {@link ArrowFlightJdbcDriver#getUrlsArgs} returns the
   * correct URL parameters.
   *
   * @throws Exception If an error occurs.
   */
  @Test
  public void testDriverUrlParsingMechanismShouldReturnTheDesiredArgsFromUrl() throws Exception {
    final ArrowFlightJdbcDriver driver = new ArrowFlightJdbcDriver();

    final Map<Object, Object> parsedArgs = driver.getUrlsArgs(
        "jdbc:arrow-flight-sql://localhost:2222/?key1=value1&key2=value2&a=b")
        .orElseThrow(() -> new RuntimeException("URL was rejected"));

    // Check size == the amount of args provided (scheme not included)
    assertEquals(5, parsedArgs.size());

    // Check host == the provided host
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.HOST.camelName()), "localhost");

    // Check port == the provided port
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.PORT.camelName()), 2222);

    // Check all other non-default arguments
    assertEquals(parsedArgs.get("key1"), "value1");
    assertEquals(parsedArgs.get("key2"), "value2");
    assertEquals(parsedArgs.get("a"), "b");
  }

  @Test
  public void testDriverUrlParsingMechanismShouldReturnTheDesiredArgsFromUrlWithSemicolon() throws Exception {
    final ArrowFlightJdbcDriver driver = new ArrowFlightJdbcDriver();
    final Map<Object, Object> parsedArgs = driver.getUrlsArgs(
        "jdbc:arrow-flight-sql://localhost:2222/;key1=value1;key2=value2;a=b")
        .orElseThrow(() -> new RuntimeException("URL was rejected"));

    // Check size == the amount of args provided (scheme not included)
    assertEquals(5, parsedArgs.size());

    // Check host == the provided host
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.HOST.camelName()), "localhost");

    // Check port == the provided port
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.PORT.camelName()), 2222);

    // Check all other non-default arguments
    assertEquals(parsedArgs.get("key1"), "value1");
    assertEquals(parsedArgs.get("key2"), "value2");
    assertEquals(parsedArgs.get("a"), "b");
  }

  @Test
  public void testDriverUrlParsingMechanismShouldReturnTheDesiredArgsFromUrlWithOneSemicolon() throws Exception {
    final ArrowFlightJdbcDriver driver = new ArrowFlightJdbcDriver();
    final Map<Object, Object> parsedArgs = driver.getUrlsArgs(
        "jdbc:arrow-flight-sql://localhost:2222/;key1=value1")
        .orElseThrow(() -> new RuntimeException("URL was rejected"));

    // Check size == the amount of args provided (scheme not included)
    assertEquals(3, parsedArgs.size());

    // Check host == the provided host
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.HOST.camelName()), "localhost");

    // Check port == the provided port
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.PORT.camelName()), 2222);

    // Check all other non-default arguments
    assertEquals(parsedArgs.get("key1"), "value1");
  }

  @Test
  public void testDriverUrlParsingMechanismShouldReturnEmptyOptionalForUnknownScheme() throws SQLException {
    final ArrowFlightJdbcDriver driver = new ArrowFlightJdbcDriver();
    assertFalse(driver.getUrlsArgs("jdbc:malformed-url-flight://localhost:2222").isPresent());
  }

  /**
   * Tests whether {@code ArrowFlightJdbcDriverTest#getUrlsArgs} returns the
   * correct URL parameters when the host is an IP Address.
   *
   * @throws Exception If an error occurs.
   */
  @Test
  public void testDriverUrlParsingMechanismShouldWorkWithIPAddress() throws Exception {
    final ArrowFlightJdbcDriver driver = new ArrowFlightJdbcDriver();
    final Map<Object, Object> parsedArgs = driver.getUrlsArgs("jdbc:arrow-flight-sql://0.0.0.0:2222")
        .orElseThrow(() -> new RuntimeException("URL was rejected"));

    // Check size == the amount of args provided (scheme not included)
    assertEquals(2, parsedArgs.size());

    // Check host == the provided host
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.HOST.camelName()), "0.0.0.0");

    // Check port == the provided port
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.PORT.camelName()), 2222);
  }

  /**
   * Tests whether {@code ArrowFlightJdbcDriverTest#getUrlsArgs} escape especial characters and returns the
   * correct URL parameters when the especial character '&' is embedded in the query parameters values.
   *
   * @throws Exception If an error occurs.
   */
  @Test
  public void testDriverUrlParsingMechanismShouldWorkWithEmbeddedEspecialCharacter()
      throws Exception {
    final ArrowFlightJdbcDriver driver = new ArrowFlightJdbcDriver();
    final Map<Object, Object> parsedArgs = driver.getUrlsArgs(
        "jdbc:arrow-flight-sql://0.0.0.0:2222?test1=test1value&test2%26continue=test2value&test3=test3value")
        .orElseThrow(() -> new RuntimeException("URL was rejected"));

    // Check size == the amount of args provided (scheme not included)
    assertEquals(5, parsedArgs.size());

    // Check host == the provided host
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.HOST.camelName()), "0.0.0.0");

    // Check port == the provided port
    assertEquals(parsedArgs.get(ArrowFlightConnectionProperty.PORT.camelName()), 2222);

    // Check all other non-default arguments
    assertEquals(parsedArgs.get("test1"), "test1value");
    assertEquals(parsedArgs.get("test2&continue"), "test2value");
    assertEquals(parsedArgs.get("test3"), "test3value");
  }
}
