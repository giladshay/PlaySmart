"""
    delete_user file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""


def delete(s, user):
    """
    delete User from db
    :param s: session
    :param user: User
    :return:
    """
    try:
        s.delete(user)
        s.commit()
    except Exception as e:
        print(e)
