from ubuntu:16.04

# Install ffmpeg
RUN apt-get update
RUN apt-get install -y ffmpeg netcat-traditional netcat-openbsd

# Expose port (is this necessary?)
EXPOSE 8999

# Add entrypoint script
RUN mkdir /entrypoint/
ADD entrypoint.sh entrypoint/entrypoint.sh

ENTRYPOINT ["entrypoint/entrypoint.sh"]
