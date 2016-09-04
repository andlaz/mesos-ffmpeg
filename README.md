# A distributed video transcoding system for Mesos

## Planned functionality for 0.1.0 

 - Take any URI ( remote or local to slaves ) via an HTTP service and feed it in to a docker container running ffmpeg
 - Proxy the output of ffmpeg back to client via HTTP
 - No streaming protocol support
 

## Planned functionality for 1.0
 - RTP over HTTP in front, for live transcoding
 - web service for offline transcoding, supporting pre-configured transcoding on filesystem events ( i.e. file uploaded )
 - Take any URI ( remote or local to slaves, including on a distributed filesystem ) via the RTP/HTTP service and feed it in to a docker container running ffmpeg
 - expose advanced transcoding options for live and offline transcoding ( bit rate, resolution, etc )
 - data locality for transcoding, work with distributed fs APIs ( HDFS, Quobyte ) to run transcoding where the data is