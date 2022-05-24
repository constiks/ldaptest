test openldap replication pair

1. put following entries to your /etc/hosts file:

`YOUR_LOCAL_IP ldap1.company.local`\
`YOUR_LOCAL_IP ldap2.company.local`

2. start containers by

`cd docker/openldap`\
`./start_pair.sh`

verify containers are running:\
docker ps

[const@arch openldap]$ ./start_pair.sh\
removing old containers, if exist...\
openldap_pair1\
openldap_pair2\
preparing volumes contents...\
starting first ldap server...\
starting second ldap server...

[const@arch openldap]$ docker ps\
CONTAINER ID   IMAGE                   COMMAND                  CREATED          STATUS          PORTS                                              NAMES\
6211dc1da240   osixia/openldap:1.5.0   "/container/tool/run…"   16 seconds ago   Up 15 seconds   636/tcp, 0.0.0.0:2389->389/tcp, :::2389->389/tcp   openldap_pair2\
f329136c85f2   osixia/openldap:1.5.0   "/container/tool/run…"   16 seconds ago   Up 15 seconds   636/tcp, 0.0.0.0:1389->389/tcp, :::1389->389/tcp   openldap_pair1

both should print users:

`ldapsearch -x -H ldap://ldap1.company.local:1389 -b dc=company,dc=local -D "cn=admin,dc=company,dc=local" -w P@ssw0rd`\
`ldapsearch -x -H ldap://ldap2.company.local:2389 -b dc=company,dc=local -D "cn=admin,dc=company,dc=local" -w P@ssw0rd`

3. compile and run test springboot app:

`mvn clean package`\
`java -jar target/ldaptest-0.0.1-SNAPSHOT.jar`

4. navigate to http://localhost:8080

login using openldap:

user/password:\
admin_user1/admin_user1\
admin_user2/admin_user2\
readonly_user1/readonly_user1\
readonly_user2/readonly_user2


