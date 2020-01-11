FROM ubuntu

RUN apt-get update
RUN apt-get install -y xvfb x11vnc x11-xkb-utils xfonts-100dpi xfonts-75dpi xfonts-scalable xfonts-cyrillic x11-apps

ADD build-scripts/xvfb.sh /etc/init.d/xvfb
RUN chmod a+x /etc/init.d/xvfb
ADD build-scripts/xvfb_run.sh /usr/bin/xvfb_run
RUN chmod a+x /usr/bin/xvfb_run

ENV DISPLAY :99

RUN apt-get install -y openjdk-8-jdk openjdk-8-jre maven
