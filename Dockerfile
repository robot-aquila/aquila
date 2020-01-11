FROM ubuntu

RUN apt-get update
RUN apt-get install -y xvfb x11vnc x11-xkb-utils xfonts-100dpi xfonts-75dpi xfonts-scalable xfonts-cyrillic x11-apps maven

ADD build-scripts/xvfb_init.sh /etc/init.d/xvfb
RUN chmod a+x /etc/init.d/xvfb
ADD build-scripts/xvfb_run.sh /usr/bin/xvfb-run
RUN chmod a+x /usr/bin/xvfb-run

ENV DISPLAY :99