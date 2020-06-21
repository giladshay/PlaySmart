"""
    create_user file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""


def create(s, user):
    """
    create user in db
    :param s: session
    :param user: User
    :return:
    """
    s.add(user)
    s.commit()
