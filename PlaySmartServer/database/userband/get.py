"""
    get_userband file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""

from database.database import *
from database.user.get import by_id


def users_by_band(s, band_id):
    """
    get all the users in the given band
    :param s: session
    :param band_id: int
    :return: lst(User)
    """
    return s.query(UserBand).filter_by(band_id=band_id).all()


def bands_by_user(s, user_id):
    """
    get all the bands that the user is a player in them
    :param s: session
    :param user_id: int
    :return: lst(Band)
    """
    return s.query(UserBand).filter_by(user_id=user_id).all()


def users_by_band_and_instrument(s, band_id, instrument):
    """
    get number of users that are in given band and play on given instrument
    :param s: session
    :param band_id: int
    :param instrument: String
    :return: int
    """
    userbands = users_by_band(s, band_id) # get all the users in band
    count = 0
    for userband in userbands:
        if by_id(s, userband.user_id).instrument == instrument:
            count += 1
    return count # return number of users