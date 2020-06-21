"""
    create_userband file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""

from database.database import UserBand


def create(s, userband):
    """
    create UserBand in db
    :param s: session
    :param userband: UserBand
    :return:
    """
    if s.query(UserBand).filter(UserBand.band_id == userband.band_id).filter(UserBand.user_id == userband.user_id).first(): # if UserBand exists already
        pass
    else:
        s.add(userband)
        s.commit()

