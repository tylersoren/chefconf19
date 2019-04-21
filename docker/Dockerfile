FROM debian:stable
LABEL maintainer="tyler_sorensen@discovery.com"

USER root
RUN apt-get update && \
      apt-get -y upgrade && \
      apt-get install -y \
              build-essential \
              curl \
              wget \
              make \
              python-pip && \
    rm -rf /var/lib/apt/lists/*

RUN pip install awscli --upgrade
RUN wget https://packages.chef.io/files/stable/chefdk/3.7.23/ubuntu/16.04/chefdk_3.7.23-1_amd64.deb -O /tmp/chefdk.deb
RUN dpkg -i /tmp/chefdk.deb && apt-get clean && rm /tmp/chefdk.deb
COPY ./Makefile /home/root/Makefile

ENV HOME=/home/root