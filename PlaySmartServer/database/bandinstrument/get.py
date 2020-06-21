"""
    get_band_instrument file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""

from sqlalchemy import and_
from database.database import *


def by_band(s, band_id):
    """
    get BandInstruments list by band_id
    :param s: session
    :param band_id: int
    :return: BandInstrument
    """
    return s.query(BandInstrument).filter_by(band_id=band_id).all()


def instruments(s, band_id):
    instruments_list = {}
    for band_instrument in s.query(BandInstrument).filter_by(band_id=band_id).all():
        instruments_list[band_instrument.instrument] = band_instrument.count
    return instruments_list


def count_instrument_in_band(s, band_id, instrument):
    return s.query(BandInstrument).filter(and_(BandInstrument.band_id == band_id, BandInstrument.instrument == instrument)).first().count


