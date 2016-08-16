FROM ibmcom/mq:8
RUN useradd scs -G mqm && \
    echo scs:scs | chpasswd

COPY config.mqsc /etc/mqm/
