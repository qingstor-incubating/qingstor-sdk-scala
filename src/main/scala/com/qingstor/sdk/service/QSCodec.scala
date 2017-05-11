package com.qingstor.sdk.service

import com.qingstor.sdk.service.Bucket._
import com.qingstor.sdk.service.QingStor.ListBucketsOutput
import com.qingstor.sdk.service.Types._
import io.circe.{Decoder, Encoder}

object QSCodec {
  object QSTypesCodec {

    implicit val decodeBucketModel: Decoder[BucketModel] = Decoder.forProduct4(
      "created", "location", "name", "url"
    )(BucketModel.apply)

    implicit val encodeBucketModel: Encoder[BucketModel] = Encoder.forProduct4(
      "created", "location", "name", "url"
    )(m => (m.created, m.location, m.name, m.uRL))

    implicit val decodeCORSRuleModel: Decoder[CORSRuleModel] = Decoder.forProduct5(
      "allowed_headers", "allowed_methods", "allowed_origin", "expose_headers", "max_age_seconds"
    )(CORSRuleModel.apply)

    implicit val encodeCORSRuleModel: Encoder[CORSRuleModel] = Encoder.forProduct5(
      "allowed_headers", "allowed_methods", "allowed_origin", "expose_headers", "max_age_seconds"
    )(m => (m.allowedHeaders, m.allowedMethods, m.allowedOrigin, m.exposeHeaders, m.maxAgeSeconds))

    implicit val decodeGranteeModel: Decoder[GranteeModel] = Decoder.forProduct3(
      "id", "name", "type"
    )(GranteeModel.apply)

    implicit val encodeGranteeModel: Encoder[GranteeModel] = Encoder.forProduct3(
      "id", "name", "type"
    )(m => (m.iD, m.name, m.typ))

    implicit val decodeIPAddressModel: Decoder[IPAddressModel] = Decoder.forProduct1(
      "source_ip"
    )(IPAddressModel.apply)

    implicit val encodeIPAddressModel: Encoder[IPAddressModel] = Encoder.forProduct1(
      "source_ip"
    )(m => (m.sourceIP))

    implicit val decodeIsNullModel: Decoder[IsNullModel] = Decoder.forProduct1(
      "Referer"
    )(IsNullModel.apply)

    implicit val encodeIsNullModel: Encoder[IsNullModel] = Encoder.forProduct1(
      "Referer"
    )(m => (m.referer))

    implicit val decodeKeyModel: Decoder[KeyModel] = Decoder.forProduct7(
      "created", "encrypted", "etag", "key", "mime_type", "modified", "size"
    )(KeyModel.apply)

    implicit val encodeKeyModel: Encoder[KeyModel] = Encoder.forProduct7(
      "created", "encrypted", "etag", "key", "mime_type", "modified", "size"
    )(m => (m.created, m.encrypted, m.etag, m.key, m.mimeType, m.modified, m.size))

    implicit val decodeKeyDeleteErrorModel: Decoder[KeyDeleteErrorModel] = Decoder.forProduct3(
      "code", "key", "message"
    )(KeyDeleteErrorModel.apply)

    implicit val encodeKeyDeleteErrorModel: Encoder[KeyDeleteErrorModel] = Encoder.forProduct3(
      "code", "key", "message"
    )(m => (m.code, m.key, m.message))

    implicit val decodeNotIPAddressModel: Decoder[NotIPAddressModel] = Decoder.forProduct1(
      "source_ip"
    )(NotIPAddressModel.apply)

    implicit val encodeNotIPAddressModel: Encoder[NotIPAddressModel] = Encoder.forProduct1(
      "source_ip"
    )(m => (m.sourceIP))

    implicit val decodeObjectPartModel: Decoder[ObjectPartModel] = Decoder.forProduct4(
      "created", "etag", "part_number", "size"
    )(ObjectPartModel.apply)

    implicit val encodeObjectPartModel: Encoder[ObjectPartModel] = Encoder.forProduct4(
      "created", "etag", "part_number", "size"
    )(m => (m.created, m.etag, m.partNumber, m.size))

    implicit val decodeOwnerModel: Decoder[OwnerModel] = Decoder.forProduct2(
      "id", "name"
    )(OwnerModel.apply)

    implicit val encodeOwnerModel: Encoder[OwnerModel] = Encoder.forProduct2(
      "id", "name"
    )(m => (m.iD, m.name))

    implicit val decodeStringLikeModel: Decoder[StringLikeModel] = Decoder.forProduct1(
      "Referer"
    )(StringLikeModel.apply)

    implicit val encodeStringLikeModel: Encoder[StringLikeModel] = Encoder.forProduct1(
      "Referer"
    )(m => (m.referer))

    implicit val decodeStringNotLikeModel: Decoder[StringNotLikeModel] = Decoder.forProduct1(
      "Referer"
    )(StringNotLikeModel.apply)

    implicit val encodeStringNotLikeModel: Encoder[StringNotLikeModel] = Encoder.forProduct1(
      "Referer"
    )(m => (m.referer))

    implicit val decodeUploadsModel: Decoder[UploadsModel] = Decoder.forProduct3(
      "created", "key", "upload_id"
    )(UploadsModel.apply)

    implicit val encodeUploadsModel: Encoder[UploadsModel] = Encoder.forProduct3(
      "created", "key", "upload_id"
    )(m => (m.created, m.key, m.uploadID))

    implicit val decodeACLModel: Decoder[ACLModel] = Decoder.forProduct2(
      "grantee", "permission"
    )(ACLModel.apply)

    implicit val encodeACLModel: Encoder[ACLModel] = Encoder.forProduct2(
      "grantee", "permission"
    )(m => (m.grantee, m.permission))

    implicit val decodeConditionModel: Decoder[ConditionModel] = Decoder.forProduct5(
      "ip_address", "is_null", "not_ip_address", "string_like", "string_not_like"
    )(ConditionModel.apply)

    implicit val encodeConditionModel: Encoder[ConditionModel] = Encoder.forProduct5(
      "ip_address", "is_null", "not_ip_address", "string_like", "string_not_like"
    )(m => (m.iPAddress, m.isNull, m.notIPAddress, m.stringLike, m.stringNotLike))

    implicit val decodeStatementModel: Decoder[StatementModel] = Decoder.forProduct6(
      "action", "condition", "effect", "id", "resource", "user"
    )(StatementModel.apply)

    implicit val encodeStatementModel: Encoder[StatementModel] = Encoder.forProduct6(
      "action", "condition", "effect", "id", "resource", "user"
    )(m => (m.action, m.condition, m.effect, m.iD, m.resource, m.user))
  }

  object QSOutputCodec {
    import QSTypesCodec._

    implicit val decodeListBucketsOutput: Decoder[ListBucketsOutput] =
      Decoder.forProduct2("buckets", "count")(ListBucketsOutput.apply)

    implicit val encodeListBucketsOutput: Encoder[ListBucketsOutput] =
      Encoder.forProduct2("buckets", "count")(o => (o.buckets, o.count))

    implicit val decodeListMultipartOutput: Decoder[ListMultipartOutput] = Decoder.forProduct2(
      "count", "object_parts"
    )(ListMultipartOutput.apply)

    implicit val encodeListMultipartOutput: Encoder[ListMultipartOutput] = Encoder.forProduct2(
      "count", "object_parts"
    )(o => (o.count, o.objectParts))

    implicit val decodeListObjectsOutput: Decoder[ListObjectsOutput] = Decoder.forProduct9(
      "common_prefixes", "delimiter", "keys", "limit", "marker", "name", "next_marker", "owner", "prefix"
    )(ListObjectsOutput.apply)

    implicit val encodeListObjectsOutput: Encoder[ListObjectsOutput] = Encoder.forProduct9(
      "common_prefixes", "delimiter", "keys", "limit", "marker", "name", "next_marker", "owner", "prefix"
    )(o => (o.commonPrefixes, o.delimiter, o.keys, o.limit, o.marker, o.name, o.nextMarker, o.owner, o.prefix))

    implicit val decodeInitiateMultipartUploadOutput:
      Decoder[InitiateMultipartUploadOutput] = Decoder.forProduct4(
      "X-QS-Encryption-Customer-Algorithm", "bucket", "key", "upload_id"
    )(InitiateMultipartUploadOutput.apply)

    implicit val encodeInitiateMultipartUploadOutput
    : Encoder[InitiateMultipartUploadOutput] = Encoder.forProduct4(
      "X-QS-Encryption-Customer-Algorithm", "bucket", "key", "upload_id"
    )(o => (o.xQSEncryptionCustomerAlgorithm, o.bucket, o.key, o.uploadID))

    implicit val decodeListMultipartUploadsOutput
    : Decoder[ListMultipartUploadsOutput] = Decoder.forProduct8(
      "common_prefixes", "delimiter", "limit", "marker", "name", "next_marker", "prefix", "uploads"
    )(ListMultipartUploadsOutput.apply)

    implicit val encodeListMultipartUploadsOutput
    : Encoder[ListMultipartUploadsOutput] = Encoder.forProduct8(
      "common_prefixes", "delimiter", "limit", "marker", "name", "next_marker", "prefix", "uploads"
    )(o => (o.commonPrefixes, o.delimiter, o.limit, o.marker, o.name, o.nextMarker, o.prefix, o.uploads))

    implicit val decodeGetBucketStatisticsOutput: Decoder[GetBucketStatisticsOutput] = Decoder.forProduct7(
      "count", "created", "location", "name", "size", "status", "url"
    )(GetBucketStatisticsOutput.apply)

    implicit val encodeGetBucketStatisticsOutput: Encoder[GetBucketStatisticsOutput] = Encoder.forProduct7(
      "count", "created", "location", "name", "size", "status", "url"
    )(o => (o.count, o.created, o.location, o.name, o.size, o.status, o.uRL))

    implicit val decodeGetBucketPolicyOutput: Decoder[GetBucketPolicyOutput] =
      Decoder.forProduct1("statement")(GetBucketPolicyOutput.apply)

    implicit val encodeGetBucketPolicyOutput: Encoder[GetBucketPolicyOutput] =
      Encoder.forProduct1("statement")(_.statement)

    implicit val decodeGetBucketExternalMirrorOutput: Decoder[GetBucketExternalMirrorOutput] =
      Decoder.forProduct1("source_site")(GetBucketExternalMirrorOutput.apply)

    implicit val encodeGetBucketExternalMirrorOutput: Encoder[GetBucketExternalMirrorOutput] =
      Encoder.forProduct1("source_site")(_.sourceSite)

    implicit val decodeGetBucketCORSOutput: Decoder[GetBucketCORSOutput] =
      Decoder.forProduct1("cors_rules")(GetBucketCORSOutput.apply)

    implicit val encodeGetBucketCORSOutput: Encoder[GetBucketCORSOutput] =
      Encoder.forProduct1("cors_rules")(_.cORSRules)

    implicit val decodeGetBucketACLOutput: Decoder[GetBucketACLOutput] =
      Decoder.forProduct2("acl", "owner")(GetBucketACLOutput.apply)

    implicit val encodeGetBucketACLOutput: Encoder[GetBucketACLOutput] =
      Encoder.forProduct2("acl", "owner")(o => (o.aCL, o.owner))

    implicit val decodeDeleteMultipleObjectsOutput: Decoder[DeleteMultipleObjectsOutput] =
      Decoder.forProduct2("deleted", "errors")(DeleteMultipleObjectsOutput.apply)

    implicit val encodeDeleteMultipleObjectsOutput: Encoder[DeleteMultipleObjectsOutput] =
      Encoder.forProduct2("deleted", "errors")(o => (o.deleted, o.errors))
  }
}
