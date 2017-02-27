package com.qingstor.sdk.service

import java.time.ZonedDateTime

import com.qingstor.sdk.constant.QSConstants

object Types {
  abstract class TypeModel

  case class BucketModel(name: String,
                         location: String,
                         url: String,
                         created: ZonedDateTime) extends TypeModel

  case class DeleteErrorModel(key: String, code: String, message: String) extends TypeModel

  case class ObjectKeyModel(key: String) extends TypeModel
  case class ObjectModel(created: String, modified: Long, encrypted: Boolean, etag: String, key: String, mime_type: String, size: Long)

  case class OwnerModel(id: String, name: String) extends TypeModel

  case class GranteeModel(`type`: String, id: Option[String] = None, name: Option[String] = None) extends TypeModel {
    require(QSConstants.GranteeTypes.contains(`type`),
      """type must be one of "%s" """.format(QSConstants.GranteeTypes.mkString(",")))
    require(`type`.equals("group")
            || (`type`.equals("user") && id != null && id.exists(_.nonEmpty)),
            """While type is "user", id can't be empty""")
  }

  case class ACLModel(grantee: GranteeModel, permission: String) extends TypeModel {
    require(QSConstants.ACLPermissions.contains(permission),
      """permission must be one of "%s" """.format(QSConstants.ACLPermissions.mkString(",")))
  }

  case class CORSRulesModel(allowed_origin: String,
                            allowed_methods: List[String],
                            allowed_headers: Option[List[String]] = None,
                            max_age_seconds: Option[Int] = None,
                            expose_headers: Option[List[String]] = None) extends TypeModel {
    require(allowed_origin != null
      && (allowed_origin.startsWith("http://")
        || allowed_origin.startsWith("https://")),
      """allowed_origin can't be empty and must start with "http://" or "https:// """)
    require(allowed_methods.nonEmpty, "allowed_methods can't be empty")
    require(allowed_methods.forall(QSConstants.CORSHttpMethods.contains(_)),
      """allowed_methods must be member(s) of "%s" """.format(QSConstants.CORSHttpMethods.mkString(",")))
  }

}
