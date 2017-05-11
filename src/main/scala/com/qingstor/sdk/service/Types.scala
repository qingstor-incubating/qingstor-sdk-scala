package com.qingstor.sdk.service

object Types {
  case class ACLModel(
      grantee: GranteeModel,
      // Permission for this grantee
      // permission's available values: READ, WRITE, FULL_CONTROL
      permission: String
  ) {

    require(grantee != null, "grantee can't be empty")

    require(permission != null, "permission can't be empty")
    require(permission.nonEmpty, """permission can't be empty""")

    require("READ, WRITE, FULL_CONTROL".split(", ").contains(permission),
            """permission can only be one of "READ, WRITE, FULL_CONTROL" """)

  }
  case class BucketModel(
      // Created time of the bucket
      created: Option[String] = None,
      // QingCloud Zone ID
      location: Option[String] = None,
      // Bucket name
      name: Option[String] = None,
      // URL to access the bucket
      uRL: Option[String] = None
  ) {}
  case class ConditionModel(
      iPAddress: Option[IPAddressModel] = None,
      isNull: Option[IsNullModel] = None,
      notIPAddress: Option[NotIPAddressModel] = None,
      stringLike: Option[StringLikeModel] = None,
      stringNotLike: Option[StringNotLikeModel] = None
  ) {}
  case class CORSRuleModel(
      // Allowed headers
      allowedHeaders: Option[List[String]] = None,
      // Allowed methods
      allowedMethods: List[String],
      // Allowed origin
      allowedOrigin: String,
      // Expose headers
      exposeHeaders: Option[List[String]] = None,
      // Max age seconds
      maxAgeSeconds: Option[Int] = None
  ) {

    require(allowedMethods != null, "allowedMethods can't be empty")
    require(allowedMethods.nonEmpty, """allowedMethods can't be empty""")

    require(allowedOrigin != null, "allowedOrigin can't be empty")
    require(allowedOrigin.nonEmpty, """allowedOrigin can't be empty""")

  }
  case class GranteeModel(
      // Grantee user ID
      iD: Option[String] = None,
      // Grantee group name
      name: Option[String] = None,
      // Grantee type
      // typ's available values: user, group
      typ: String
  ) {

    require(typ != null, "typ can't be empty")
    require(typ.nonEmpty, """typ can't be empty""")

    require("user, group".split(", ").contains(typ),
            """typ can only be one of "user, group" """)

  }
  case class IPAddressModel(
      // Source IP
      sourceIP: Option[List[String]] = None
  ) {}
  case class IsNullModel(
      // Refer url
      referer: Option[Boolean] = None
  ) {}
  case class KeyModel(
      // Object created time
      created: Option[String] = None,
      // Whether this key is encrypted
      encrypted: Option[Boolean] = None,
      // MD5sum of the object
      etag: Option[String] = None,
      // Object key
      key: Option[String] = None,
      // MIME type of the object
      mimeType: Option[String] = None,
      // Last modified time in unix time format
      modified: Option[Int] = None,
      // Object content size
      size: Option[Int] = None
  ) {}
  case class KeyDeleteErrorModel(
      // Error code
      code: Option[String] = None,
      // Object key
      key: Option[String] = None,
      // Error message
      message: Option[String] = None
  ) {}
  case class NotIPAddressModel(
      // Source IP
      sourceIP: Option[List[String]] = None
  ) {}
  case class ObjectPartModel(
      // Object part created time
      created: Option[String] = None,
      // MD5sum of the object part
      etag: Option[String] = None,
      // Object part number
      partNumber: Int,
      // Object part size
      size: Option[Int] = None
  ) {}
  case class OwnerModel(
      // User ID
      iD: Option[String] = None,
      // Username
      name: Option[String] = None
  ) {}
  case class StatementModel(
      // QingStor API methods
      action: List[String],
      condition: Option[ConditionModel] = None,
      // Statement effect
      // effect's available values: allow, deny
      effect: String,
      // Bucket policy id, must be unique
      iD: String,
      // The resources to apply bucket policy
      resource: Option[List[String]] = None,
      // The user to apply bucket policy
      user: List[String]
  ) {

    require(action != null, "action can't be empty")
    require(action.nonEmpty, """action can't be empty""")

    require(effect != null, "effect can't be empty")
    require(effect.nonEmpty, """effect can't be empty""")

    require("allow, deny".split(", ").contains(effect),
            """effect can only be one of "allow, deny" """)
    require(iD != null, "iD can't be empty")
    require(iD.nonEmpty, """iD can't be empty""")

    require(user != null, "user can't be empty")
    require(user.nonEmpty, """user can't be empty""")

  }
  case class StringLikeModel(
      // Refer url
      referer: Option[List[String]] = None
  ) {}
  case class StringNotLikeModel(
      // Refer url
      referer: Option[List[String]] = None
  ) {}
  case class UploadsModel(
      // Object part created time
      created: Option[String] = None,
      // Object key
      key: Option[String] = None,
      // Object upload id
      uploadID: Option[String] = None
  ) {}

}
