# QingStor Service Usage Guide

Import the QingStor and initialize service with a config, and you are ready 
to use the initialized service. `QingStor` only contains one API, and it is 
`listBuckets`.To use bucket or object related APIs, you need to initialize
a `Bucket` or an `Object`.

Each API function take a `xxxInput` class and return a `Future[xxxOutput]` 
class. The Input class consists of request params, request headers, request 
elements and request body, and the Output holds the HTTP status code, QingStor 
request ID, response headers, response elements, response body. if error 
occurred, a `QingStorException` will be thrown, it contains an `ErrorMessage`
instance which consists `request_id`, `status_code`, `code`, `message` and 
`url`

### Code Snippet

Initialize the QingStor service with a configuration
```scala
val qsService = QingStor(config)
```

List buckets
```scala
val outputFuture = qsService.listBuckets(QingStor.ListBucketsInput())
val listBucketsOutput = Await.result(outputFuture, Duration.Inf)

// Print HTTP status code.
// Example: 200
println(listBucketsOutput.statusCode.getOrElse(-1))

// Print the count of buckets.
// Example: 5
println(listBucketsOutput.count.getOrElse(-1))

// Print the first bucket name.
// Example: "test-bucket"
println(listBucketsOutput.buckets.flatMap(_.head.name).getOrElse("No buckets"))
```

Initialize a QingStor bucket
```scala
val bucket = Bucket(config, "test-bucke", "pek3a")
```

List objects in the bucket
```scala
val outputFuture = bucket.listObjects(Bucket.ListObjectsInput())
val listObjectsOutput = Await.result(outputFuture, Duration.Inf)

// Print the HTTP status code.
// Example: 200
println(listObjectsOutput.statusCode.getOrElse(-1))

// Print the key count.
// Example: 7
println(listObjectsOutput.keys.map(_.length).getOrElse(-1))
```

Set ACL of the bucket
```scala
val input = PutBucketACLInput(
  aCL = List(ACLModel(
    grantee = GranteeModel(
      typ = "user",
      id = Some("usr-xxxxxxxx")
    ),
    permission = "FULL_CONTROL"
  ))
)
val outputFuture = bucket.putACL(input)
val putBucketACLOutput = Await.result(outputFuture, Duration.Inf)

// Print the HTTP status code.
// Example: 200
println(putBucketACLOutput.statusCode.getOrElse(-1))
```

Put object
```scala
// Open file
val file = new File("/tmp/test.jpg")
val input = PutObjectInput(
  // Because this SDK used akka-http as http library, it's useless to set Content-Length here
  contentLength = file.length().toInt,
  body = file
)
val outputFuture = bucket.putObject("test.jpg", input)
val putObjectOutput = Await.result(outputFuture, Duration.Inf)
file.close()

// Print the HTTP status code.
// Example: 201
println(putObjectOutput.statusCode.getOrElse(-1))
```

Initialize Multipart Upload
```scala
val input = InitiateMultipartUploadInput(
  contentType = Some("video/quicktime")
)
val outputFuture = bucket.initiateMultipartUpload("QingCloudInsight.mov", input)
val initiateMultipartUploadOutput = Await.result(outputFuture, Duration.Inf)

// Print the upload ID.
// Example: "9d37dd6ccee643075ca4e597ad65655c"
println(initiateMultipartUploadOutput.uploadID.getOrElse(""))
```

Upload Multipart
```scala
// Upload the 1st part
val input = UploadMultipartInput(
  partNumber = 0,
  uploadID = "9d37dd6ccee643075ca4e597ad65655c",
  body = file0
)
val of = bucket.uploadMultipart("QingCloudInsight.mov", input)
val uploadMultipartOutput = Await.result(of, Duration.Inf)

// Print the HTTP status code.
// Example: 201
println(uploadMultipartOutput.statusCode.getOrElse(-1))

// Upload the 2nd part
val input = UploadMultipartInput(
  partNumber = 1,
  uploadID = "9d37dd6ccee643075ca4e597ad65655c",
  body = file1
)
val of = bucket.uploadMultipart("QingCloudInsight.mov", input)
val uploadMultipartOutput = Await.result(of, Duration.Inf)

// Print the HTTP status code.
// Example: 201
println(uploadMultipartOutput.statusCode.getOrElse(-1))
```

Complete Multipart Upload
```scala
val input = CompleteMultipartUploadInput(
  uploadID = "9d37dd6ccee643075ca4e597ad65655c",
  objectParts = Some(Lsit(
    ObjectPartModel(partNumber = 0),
    ObjectPartModel(partNumber = 1)
  ))
)
val outputFuture = bucket.completeMultipartUpload("QingCloudInsight.mov", input)
val completeMultipartUploadOutput = Await.result(outputFuture, Duration.Inf)

// Print the HTTP status code.
// Example: 200
println(completeMultipartUploadOutput.statusCode.getOrElse(-1))
```