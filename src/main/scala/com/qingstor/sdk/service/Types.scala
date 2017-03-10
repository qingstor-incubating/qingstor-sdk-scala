package com.qingstor.sdk.service

import java.time.ZonedDateTime

object Types {
  case class ACLModel(
      `grantee`: GranteeModel,
      // Permission for this grantee
      // permission's available values: READ, WRITE, FULL_CONTROL
      `permission`: String
  ) {

    require(`grantee` != null, "`grantee` can't be empty")

    require(`permission` != null, "`permission` can't be empty")
    require(`permission`.nonEmpty, """`permission` can't be empty""")

    require("READ, WRITE, FULL_CONTROL".split(", ").contains(`permission`),
            """`permission` can only be one of "READ, WRITE, FULL_CONTROL" """)

  }
  case class BucketModel(
      // Created time of the bucket
      `created`: Option[String] = None,
      // QingCloud Zone ID
      `location`: Option[String] = None,
      // Bucket name
      `name`: Option[String] = None,
      // URL to access the bucket
      `url`: Option[String] = None
  ) {}
  case class ConditionModel(
      `ip_address`: Option[IPAddressModel] = None,
      `is_null`: Option[IsNullModel] = None,
      `not_ip_address`: Option[NotIPAddressModel] = None,
      `string_like`: Option[StringLikeModel] = None,
      `string_not_like`: Option[StringNotLikeModel] = None
  ) {}
  case class CORSRuleModel(
      // Allowed headers
      `allowed_headers`: Option[List[String]] = None,
      // Allowed methods
      `allowed_methods`: List[String],
      // Allowed origin
      `allowed_origin`: String,
      // Expose headers
      `expose_headers`: Option[List[String]] = None,
      // Max age seconds
      `max_age_seconds`: Option[Int] = None
  ) {

    require(`allowed_methods` != null, "`allowed_methods` can't be empty")
    require(`allowed_methods`.nonEmpty, """`allowed_methods` can't be empty""")

    require(`allowed_origin` != null, "`allowed_origin` can't be empty")
    require(`allowed_origin`.nonEmpty, """`allowed_origin` can't be empty""")

  }
  case class GranteeModel(
      // Grantee user ID
      `id`: Option[String] = None,
      // Grantee group name
      `name`: Option[String] = None,
      // Grantee type
      // type's available values: user, group
      `type`: String
  ) {

    require(`type` != null, "`type` can't be empty")
    require(`type`.nonEmpty, """`type` can't be empty""")

    require("user, group".split(", ").contains(`type`),
            """`type` can only be one of "user, group" """)

  }
  case class IPAddressModel(
      // Source IP
      `source_ip`: Option[List[String]] = None
  ) {}
  case class IsNullModel(
      // Refer url
      `Referer`: Option[Boolean] = None
  ) {}
  case class KeyModel(
      // Object created time
      `created`: Option[String] = None,
      // MD5sum of the object
      `etag`: Option[String] = None,
      // Object key
      `key`: Option[String] = None,
      // MIME type of the object
      `mime_type`: Option[String] = None,
      // Last modified time in unix time format
      `modified`: Option[Int] = None,
      // Object content size
      `size`: Option[Int] = None
  ) {}
  case class KeyDeleteErrorModel(
      // Error code
      `code`: Option[String] = None,
      // Object key
      `key`: Option[String] = None,
      // Error message
      `message`: Option[String] = None
  ) {}
  case class NotIPAddressModel(
      // Source IP
      `source_ip`: Option[List[String]] = None
  ) {}
  case class ObjectPartModel(
      // Object part created time
      `created`: Option[String] = None,
      // MD5sum of the object part
      `etag`: Option[String] = None,
      // Object part number
      `part_number`: Int,
      // Object part size
      `size`: Option[Int] = None
  ) {}
  case class OwnerModel(
      // User ID
      `id`: Option[String] = None,
      // Username
      `name`: Option[String] = None
  ) {}
  case class StatementModel(
      // QingStor API methods
      `action`: List[String],
      `condition`: Option[ConditionModel] = None,
      // Statement effect
      // effect's available values: allow, deny
      `effect`: String,
      // Bucket policy id, must be unique
      `id`: String,
      // The resources to apply bucket policy
      `resource`: List[String],
      // The user to apply bucket policy
      `user`: List[String]
  ) {

    require(`action` != null, "`action` can't be empty")
    require(`action`.nonEmpty, """`action` can't be empty""")

    require(`effect` != null, "`effect` can't be empty")
    require(`effect`.nonEmpty, """`effect` can't be empty""")

    require("allow, deny".split(", ").contains(`effect`),
            """`effect` can only be one of "allow, deny" """)
    require(`id` != null, "`id` can't be empty")
    require(`id`.nonEmpty, """`id` can't be empty""")

    require(`resource` != null, "`resource` can't be empty")
    require(`resource`.nonEmpty, """`resource` can't be empty""")

    require(`user` != null, "`user` can't be empty")
    require(`user`.nonEmpty, """`user` can't be empty""")

  }
  case class StringLikeModel(
      // Refer url
      `Referer`: Option[List[String]] = None
  ) {}
  case class StringNotLikeModel(
      // Refer url
      `Referer`: Option[List[String]] = None
  ) {}
}
