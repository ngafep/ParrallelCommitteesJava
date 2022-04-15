##
# multi-build args
##
ARG AUTHORS="plateaujava.esa@engie.com"
ARG USER="atlas"

##
# builder stage
##
FROM eclipse-temurin:17-focal as jre-build

#> define args & labels
ARG USER
ARG AUTHORS
ARG HOMEDIR="/${USER}"
LABEL org.engie.image.authors=${AUTHORS}

#> create a JRE
RUN ${JAVA_HOME}/bin/jlink \
        --add-modules java.base \
        --add-modules java.logging \
        --add-modules java.management \
        --add-modules java.sql \
        --add-modules java.xml.crypto \
        --add-modules java.naming \
        --add-modules java.desktop \
        --add-modules jdk.unsupported \
        --add-modules java.sql.rowset \
        --add-modules java.security.jgss \
        --add-modules java.security.sasl \
        --add-modules java.instrument \
        --add-modules java.prefs \
        --strip-debug \
        --no-man-pages \
        --no-header-files \
        --compress=2 \
        --output /opt/jre-minimal

WORKDIR ${HOMEDIR}

#> extract the application layers from the jar
ARG JAR_FILE_EXE="target/PC-1.0-SNAPSHOT-jar-with-dependencies.jar"
COPY ${JAR_FILE_EXE} application.jar
COPY *.json .

#RUN java -Djarmode=layertools -jar application.jar extract

#> download tini
ENV TINI_VERSION v0.19.0
RUN curl -L https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini -o /tini
RUN chmod +x /tini

##
# runner stage
##
FROM ubuntu:focal

#> define args & labels
ARG USER
ARG AUTHORS
ARG HOMEDIR="/${USER}"
LABEL org.engie.image.authors=${AUTHORS}

#> create user & group
RUN useradd --home-dir ${HOMEDIR} --user-group ${USER}

#> setup JRE
ENV JAVA_HOME=/opt/java/jre-minimal
ENV PATH "${JAVA_HOME}/bin:${PATH}"
COPY --from=jre-build /opt/jre-minimal ${JAVA_HOME}

#> required because of a kaniko bug
RUN mkdir -p ${HOMEDIR}/tmp/ && chown -R ${USER}:${USER} ${HOMEDIR}

#> switch user
USER ${USER}
WORKDIR ${HOMEDIR}

#> import tini
COPY --from=jre-build --chown=${USER} /tini /usr/local/bin/tini
COPY --from=jre-build --chown=${USER} ${HOMEDIR}/*.json .
COPY --from=jre-build --chown=${USER} ${HOMEDIR}/application.jar .

CMD ["sleep", "infinity"]