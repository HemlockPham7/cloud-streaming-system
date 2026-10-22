# cloud-streaming-system
Feature 1 - Set scheduler to upload image to s3 storage and change the path to image. Feature 2 - Uploading video and streaming video by hls. 

```
GET videos/_search
{
  "query": {
    "match_all": {}
  }
}

GET videos/_mapping

GET _cat/indices/videos?v

GET videos/_search
{
  "size": 0,
  "suggest": {
    "video-suggest": {
      "prefix": "Viet N",
      "completion": {
        "field": "search_term",
        "size": 5,
        "skip_duplicates": true,
        "fuzzy": {
          "fuzziness": 2
        }
      }
    }
  }
}

GET videos/_search
{
  "from": 0,
  "size": 10,
  "query": {
    "bool": {
      "must": [
        {
          "multi_match": {
            "query": "Animal",
            "fields": [
              "title^3",
              "description",
              "category",
              "author"
            ],
            "type": "most_fields",
            "fuzziness": 1,
            "prefix_length": 2,
            "operator": "or"
          }
        }
      ],
      "filter": [
        {
          "range": {
            "view_count": {
              "gte": 1000
            }
          }
        },
        {
          "range": {
            "like_count": {
              "gte": 100
            }
          }
        }
      ],
      "should": [
        {
          "term": {
            "category.raw": {
              "value": "Animal",
              "boost": 5,
              "case_insentitive": true
            }
          }
        },
        {
          "term": {
            "author.raw": {
              "value": "Animal",
              "boost": 5,
              "case_insentitive": true
            }
          }
        }
      ]
    }
  },
  "sort": [
    {
      "_score": {
        "order": "desc"
      }
    }
  ]
}
```

```
{
  "took": 14,
  "timed_out": false,
  "_shards": {
    "total": 1,
    "successful": 1,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": {
      "value": 2,
      "relation": "eq"
    },
    "max_score": 0.36464313,
    "hits": [
      {
        "_index": "videos",
        "_id": "b16291e1-c03a-432d-9aef-40d78d70adaa",
        "_score": 0.36464313,
        "_source": {
          "_class": "com.streamingsystem.cloudservice.model.VideoSearchDocument",
          "id": "b16291e1-c03a-432d-9aef-40d78d70adaa",
          "title": "Beige Dog",
          "description": "One of the most rare dog breed in Viet Nam, however it is very popular and well-known for its cuteness and fluffly white fur. They are also known for its strong connections with human.",
          "author": "Viet Hoang",
          "thumbnail_key": "videos/b16291e1-c03a-432d-9aef-40d78d70adaa/thumbnail.png",
          "thumbnail_type": "image/png",
          "category": "Animal",
          "view_count": 2836532,
          "like_count": 4951064,
          "created_at": "2026-10-11T13:02:11.829243"
        }
      },
      {
        "_index": "videos",
        "_id": "0fd2d9c3-4f87-47ba-8c3a-09009878294a",
        "_score": 0.36464313,
        "_source": {
          "_class": "com.streamingsystem.cloudservice.model.VideoSearchDocument",
          "id": "0fd2d9c3-4f87-47ba-8c3a-09009878294a",
          "title": "Beige Puppy",
          "description": "A video clip of 3 silly dogs that they are looking at you and you adorably. Watch out else if they would take your hearts.",
          "author": "Phuong Ly",
          "thumbnail_key": "videos/0fd2d9c3-4f87-47ba-8c3a-09009878294a/thumbnail.png",
          "thumbnail_type": "image/png",
          "category": "Animal",
          "view_count": 357405,
          "like_count": 2875264,
          "created_at": "2026-10-11T13:12:20.892484"
        }
      }
    ]
  }
}
```
---

```
{
  "data": [
    {
      "id": "0fd2d9c3-4f87-47ba-8c3a-09009878294a",
      "title": "Beige Puppy",
      "description": "A video clip of 3 silly dogs that they are looking at you and you adorably. Watch out else if they would take your hearts.",
      "author": "Phuong Ly",
      "thumbnailKey": "videos/0fd2d9c3-4f87-47ba-8c3a-09009878294a/thumbnail.png",
      "thumbnailType": "image/png",
      "category": "Animal",
      "viewCount": 357405,
      "likeCount": 2875264,
      "createdAt": "2026-10-11T13:12:20.892484"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 1,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

## cloudfront

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
