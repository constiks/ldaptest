#!/bin/bash

echo removing old containers, if exist...
docker rm -fv openldap_pair1
docker rm -fv openldap_pair2

echo preparing volumes contents...
rm -rf /opt/openldap
mkdir -p /opt/openldap
cp bootstrap.ldif /opt/openldap

echo starting first ldap server...
LDAP1_CID=$(docker run \
    --name openldap_pair1 \
    --hostname ldap1.company.local \
    --env HOSTNAME="ldap1.company.local" \
    --env LDAP_ORGANISATION="Company" \
    --env LDAP_DOMAIN="company.local" \
    --env LDAP_ADMIN_PASSWORD="P@ssw0rd" \
    --env LDAP_REPLICATION=true \
    --env LDAP_REPLICATION_HOSTS="#PYTHON2BASH:['ldap://ldap1.company.local','ldap://ldap2.company.local']" \
    --env LDAP_LOG_LEVEL=256 \
    --volume /opt/openldap/bootstrap.ldif:/container/service/slapd/assets/config/bootstrap/ldif/custom/bootstrap.ldif \
    -p 1389:389 \
    --detach osixia/openldap:1.5.0 --copy-service --loglevel debug)
LDAP1_IP=$(docker inspect -f "{{ .NetworkSettings.IPAddress }}" $LDAP1_CID)

echo starting second ldap server...
LDAP2_CID=$(docker run \
    --name openldap_pair2 \
    --hostname ldap2.company.local \
    --env HOSTNAME="ldap2.company.local" \
    --env LDAP_ORGANISATION="Company" \
    --env LDAP_DOMAIN="company.local" \
    --env LDAP_ADMIN_PASSWORD="P@ssw0rd" \
    --env LDAP_REPLICATION=true \
    --env LDAP_REPLICATION_HOSTS="#PYTHON2BASH:['ldap://ldap1.company.local','ldap://ldap2.company.local']" \
    --env LDAP_LOG_LEVEL=256 \
    -p 2389:389 \
    --detach osixia/openldap:1.5.0 --copy-service --loglevel debug)
LDAP2_IP=$(docker inspect -f "{{ .NetworkSettings.IPAddress }}" $LDAP2_CID)

docker exec $LDAP1_CID bash -c "echo $LDAP2_IP ldap2.company.local >> /etc/hosts"
docker exec $LDAP2_CID bash -c "echo $LDAP1_IP ldap1.company.local >> /etc/hosts"

