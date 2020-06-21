"""
    create_band file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""


def create(s, band):
    """
    creates band in db
    :param s: session
    :param band: Band
    :return:
    """
    s.add(band)
    s.commit()
