FROM ts-docker-repo.turkiyesigorta.com.tr/ts-docker/keycloak:23.0.4
#FROM quay.io/keycloak/keycloak:23.0.4

ENV LANG=tr_TR.UTF-8 LANGUAGE=tr_TR.UTF-8

COPY ./themes/ts-keycloak-theme* /opt/keycloak/themes/ts-keycloak-theme
COPY ./target/* /opt/keycloak/providers/

