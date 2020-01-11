FROM ubuntu

ADD build-scripts/xvfb.sh /etc/init.d/xvfb
ADD build-scripts/xvfb_run.sh /usr/bin/xvfb_run
RUN chmod a+x /etc/init.d/xvfb \
    && chmod a+x /usr/bin/xvfb_run \
    && apt-get update \
    && apt-get install -y --no-install-recommends \
        xvfb x11vnc x11-xkb-utils xfonts-100dpi xfonts-75dpi xfonts-scalable xfonts-cyrillic x11-apps \
        openjdk-8-jdk openjdk-8-jre \
        ca-certificates curl firefox \
    && apt-get install -y --no-install-recommends maven \
    && curl -L https://github.com/mozilla/geckodriver/releases/download/v0.26.0/geckodriver-v0.26.0-linux64.tar.gz | tar xz -C /usr/local/bin \
    && rm -fr /var/lib/apt/lists/*

ENV DISPLAY :99
