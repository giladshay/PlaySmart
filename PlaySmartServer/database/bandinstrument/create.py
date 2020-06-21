"""
    create_band_instrument file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""


def create(s, band_instrument):
    """
    creates BandInstrument in db
    :param s: session
    :param band_instrument: BandInstrument
    :return:
    """
    s.add(band_instrument)
    s.commit()

