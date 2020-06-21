"""
    get_band file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""


from database.database import *


def all(s):
    """
    get all bands from db
    :param s: session
    :return: lst(Band)
    """
    return s.query(Band).all()


def by_id(s, band_id):
    """
    get band by its id
    :param s: session
    :param band_id: int
    :return: Band
    """
    return s.query(Band).filter_by(id=band_id).first()


def by_bandname(s, bandname):
    """
    get band by its name
    :param s: session
    :param bandname: String
    :return: Band
    """
    return s.query(Band).filter_by(name=bandname).first()


def convert_userband_list(s, userband_list):
    """
    convert list of UserBands to list of Bands
    :param s: session
    :param userband_list: lst(UserBand)
    :return: lst(Band)
    """
    bands = []
    for userband in userband_list:
        band = s.query(Band).filter_by(id=userband.band_id).first()
        bands.append({"name": band.name, "admin": band.admin_id, "genre": band.genre})
    return bands


def to_json(band):
    """
    convert band to json format
    :param band: Band
    :return: Dictionary
    """
    return {"id": band.id, "name": band.name, "genre": band.genre, "admin": band.admin_id}