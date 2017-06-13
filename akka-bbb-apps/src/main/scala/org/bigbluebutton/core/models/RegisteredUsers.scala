package org.bigbluebutton.core.models

object RegisteredUsers {
  def create(userId: String, extId: String, name: String, roles: String,
    token: String, avatar: String, guest: Boolean, authenticated: Boolean,
    waitingForAcceptance: Boolean, users: RegisteredUsers): RegisteredUser = {
    val ru = new RegisteredUser(userId, extId, name, roles, token, avatar, guest, authenticated, waitingForAcceptance)
    users.save(ru)
    ru
  }

  def findWithToken(token: String, users: RegisteredUsers): Option[RegisteredUser] = {
    users.toVector.find(u => u.authToken == token)
  }

  def findWithUserId(id: String, users: RegisteredUsers): Option[RegisteredUser] = {
    users.toVector.find(ru => id == ru.id)
  }

  def getRegisteredUserWithToken(token: String, userId: String, regUsers: RegisteredUsers): Option[RegisteredUser] = {
    def isSameUserId(ru: RegisteredUser, userId: String): Option[RegisteredUser] = {
      if (userId.startsWith(ru.id)) {
        Some(ru)
      } else {
        None
      }
    }

    for {
      ru <- RegisteredUsers.findWithToken(token, regUsers)
      user <- isSameUserId(ru, userId)
    } yield user
  }

  def updateRegUser(uvo: UserVO, users: RegisteredUsers) {
    for {
      ru <- RegisteredUsers.findWithUserId(uvo.id, users)
      regUser = new RegisteredUser(uvo.id, uvo.externalId, uvo.name, uvo.role, ru.authToken,
        uvo.avatarURL, uvo.guest, uvo.authed, uvo.waitingForAcceptance)
    } yield users.save(regUser)
  }

  def remove(id: String, users: RegisteredUsers): Option[RegisteredUser] = {
    users.delete(id)
  }

}

class RegisteredUsers {
  private var regUsers = new collection.immutable.HashMap[String, RegisteredUser]

  private def toVector: Vector[RegisteredUser] = regUsers.values.toVector

  private def save(user: RegisteredUser): Vector[RegisteredUser] = {
    regUsers += user.authToken -> user
    regUsers.values.toVector
  }

  private def delete(id: String): Option[RegisteredUser] = {
    val ru = regUsers.get(id)
    ru foreach { u => regUsers -= u.authToken }
    ru
  }
}

case class RegisteredUser(id: String, externId: String, name: String, role: String,
  authToken: String, avatarURL: String, guest: Boolean,
  authed: Boolean, waitingForAcceptance: Boolean)

