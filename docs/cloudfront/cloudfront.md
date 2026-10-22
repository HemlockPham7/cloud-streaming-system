# cloudfront

```
http://ekyv6n0z002g1o.cloudfront.localhost.floci.io:4566/videos/1bf9472d-24bc-4713-8c92-2888aac9477d/original.mp4
```

```
$ curl -i \
>   -H "Host: ekyv6n0z002g1o.cloudfront.net" \
>   "http://localhost:4566/videos/1bf9472d-24bc-4713-8c92-2888aac9477d/original.mp4"

HTTP/1.1 200 OK
Accept-Ranges: bytes
Content-Length: 9934733
Content-Type: video/mp4
Date: Thu, 22 Oct 2026 02:49:13 GMT
ETag: "bf4133d0e869858619263933dbb91728"
Last-Modified: Thu, 22 Oct 2026 02:16:19 GMT
x-amz-id-2: a6bb07ec-024b-4b81-a84a-f62cc352f962
x-amz-request-id: a6bb07ec-024b-4b81-a84a-f62cc352f962
x-amz-storage-class: STANDARD
x-amzn-RequestId: a6bb07ec-024b-4b81-a84a-f62cc352f962

```

```
basePath: "http://ekyv6n0z002g1o.cloudfront.localhost.floci.io:4566"
masterPlaylistContent: "#EXTM3U\n#EXT-X-VERSION:3\n\n#EXT-X-STREAM-INF:BANDWIDTH=2800000,RESOLUTION=1280x720,CODECS=\"avc1.42e01e,mp4a.40.2\"\n720p/index.m3u8\n\n#EXT-X-STREAM-INF:BANDWIDTH=1200000,RESOLUTION=854x480,CODECS=\"avc1.42e01e,mp4a.40.2\"\n480p/index.m3u8\n\n"
masterPlaylistPath: "videos/1bf9472d-24bc-4713-8c92-2888aac9477d/hls/master.m3u8"
```