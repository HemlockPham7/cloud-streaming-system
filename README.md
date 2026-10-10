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
  "size": 5,
  "_source": [
    "title",
    "description",
    "author"
  ],
  "query": {
    "multi_match": {
      "query": "Viet Hoang",
      "fields": [
        "title^3",
        "description",
        "author^2"
      ],
      "type": "best_fields",
      "operator": "or",
      "fuzziness": "AUTO"
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
            "query": "Viet nam",
            "fields": [
              "title^3",
              "description",
              "category^2",
              "author^2"
            ],
            "type": "best_fields",
            "fuzziness": 1
          }
        }
      ],
      "filter": [
        {
          "range": {
            "viewCount": {
              "gte": 1000
            }
          }
        },
        {
          "range": {
            "likeCount": {
              "gte": 100
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
    },
    {
      "viewCount": {
        "order": "desc"
      }
    }
  ]
}
```