"""
    get_user file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""

from database.database import *


def by_id(session, user_id):
    """
    get User from db by id
    :param session: session
    :param user_id: int
    :return: User
    """
    try:
        return session.query(User).filter_by(id=user_id).first()
    except Exception as e:
        print(e)
        return None


def by_username(s, username):
    """
    get User from db by username
    :param s: session
    :param username: String
    :return: User
    """
    try:
        return s.query(User).filter_by(username=username).first()
    except Exception as e:
        print(e)
        return None


def to_json(user):
    """
    convert User to json format
    :param user: User
    :return: Dictionary
    """
    return {"id": user.id, "username": user.username, "instrument": user.instrument, "genre": user.genre,
                    "number": user.phone_number, "email": user.email}