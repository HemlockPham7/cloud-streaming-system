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
            "query": "Viet N",
            "fields": [
              "title^3",
              "description",
              "category^2",
              "author^2"
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