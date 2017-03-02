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

  case class PartModel(part_number: Int, size: Option[Long] = None,
                       created: Option[ZonedDateTime] = None, etag: Option[String] = None)

  case class StringModel(Referer: List[String])
  case class IPModel(source_ip: List[String])
  case class NullModel(Referer: Boolean)
  case class ConditionModel(string_like: Option[StringModel] = None, string_not_like: Option[StringModel] = None,
                            ip_address: Option[IPModel] = None, not_ip_address: Option[IPModel] = None,
                            is_null: Option[NullModel] = None)
  case class StatementModel(id: String, user: Either[String, List[String]], action: List[String],
                            effect: String, resource: List[String], condition: List[ConditionModel]) {
    require(action.nonEmpty, "StatementModel: action can't be empty!")
    require(effect != null && effect.nonEmpty, "StatementModel: effect can't be empty")
    require(QSConstants.StatementEffects.contains(effect),
      "StatementModel: value of effect must be one of \"%s\"".format(QSConstants.StatementEffects.mkString(",")))
    require(id != null && id.nonEmpty, "StatementModel: id can't be empty!")
    require(resource != null && resource.nonEmpty, "StatementModel: resource can't be empty!")
    require(user.fold[String](str => str, list => list.mkString("")).nonEmpty, "StatementModel: user can't be empty!")
  }
}
