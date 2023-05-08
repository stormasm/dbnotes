
After installing minio from brew...  Here is what I get

Command-line Access: https://docs.min.io/docs/minio-client-quickstart-guide

Object API (Amazon S3 compatible):
   Go:         https://docs.min.io/docs/golang-client-quickstart-guide
   Java:       https://docs.min.io/docs/java-client-quickstart-guide
   Python:     https://docs.min.io/docs/python-client-quickstart-guide
   JavaScript: https://docs.min.io/docs/javascript-client-quickstart-guide
   .NET:       https://docs.min.io/docs/dotnet-client-quickstart-guide

Talk to the community: https://slack.min.io
==> Get started:
NAME:
  minio server - start object storage server

USAGE:
  minio server [FLAGS] DIR1 [DIR2..]
  minio server [FLAGS] DIR{1...64}
  minio server [FLAGS] DIR{1...64} DIR{65...128}

DIR:
  DIR points to a directory on a filesystem. When you want to combine
  multiple drives into a single large system, pass one directory per
  filesystem separated by space. You may also use a '...' convention
  to abbreviate the directory arguments. Remote directories in a
  distributed setup are encoded as HTTP(s) URIs.

FLAGS:
  --address value              bind to a specific ADDRESS:PORT, ADDRESS can be an IP or hostname (default: ":9000") [$MINIO_ADDRESS]
  --console-address value      bind to a specific ADDRESS:PORT for embedded Console UI, ADDRESS can be an IP or hostname [$MINIO_CONSOLE_ADDRESS]
  --ftp value                  enable and configure an FTP(Secure) server
  --sftp value                 enable and configure an SFTP server
  --certs-dir value, -S value  path to certs directory (default: "/Users/ma/.minio/certs")
  --quiet                      disable startup and info messages
  --anonymous                  hide sensitive information from logging
  --json                       output logs in JSON format
  --help, -h                   show help

EXAMPLES:
  1. Start MinIO server on "/home/shared" directory.
     $ minio server /home/shared

  2. Start single node server with 64 local drives "/mnt/data1" to "/mnt/data64".
     $ minio server /mnt/data{1...64}

  3. Start distributed MinIO server on an 32 node setup with 32 drives each, run following command on all the nodes
     $ minio server http://node{1...32}.example.com/mnt/export{1...32}

  4. Start distributed MinIO server in an expanded setup, run the following command on all the nodes
     $ minio server http://node{1...16}.example.com/mnt/export{1...32} \
            http://node{17...64}.example.com/mnt/export{1...64}

  5. Start distributed MinIO server, with FTP and SFTP servers on all interfaces via port 8021, 8022 respectively
     $ minio server http://node{1...4}.example.com/mnt/export{1...4} \
           --ftp="address=:8021" --ftp="passive-port-range=30000-40000" \
           --sftp="address=:8022" --sftp="ssh-private-key=${HOME}/.ssh/id_rsa"
🍺  /opt/homebrew/Cellar/minio/RELEASE.2023-05-04T21-44-30Z_1: 3 files, 100.9MB, built in 2 seconds
==> Running `brew cleanup minio`...
