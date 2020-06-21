"""
    database file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""

from sqlalchemy import create_engine, Column, Integer, String, Boolean
from sqlalchemy.ext.declarative import declarative_base

engine = create_engine("sqlite:///D:\\user\\PlaySmartServer\\Db\\data.db", echo=True)
Base = declarative_base()


class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True)
    username = Column(String, unique=True)
    password = Column(String)
    email = Column(String)
    phone_number = Column(String)
    instrument = Column(String)
    genre = Column(String)

    def __init__(self, username, password, email, phone_number, instrument, genre):
        self.username = username
        self.password = password
        self.email = email
        self.phone_number = phone_number
        self.instrument = instrument
        self.genre = genre

    def __repr__(self):
        if self.id:
            return "<User(id=%d, username='%s', password='%s', email='%s', number='%s', instrument=%s, genre=%s>" % \
                   (self.id, self.username, self.password, self.email, self.phone_number, self.instrument, self.genre)
        return "<User(username='%s', password='%s', email='%s', number='%s', instrument='%s', genre='%s')>" % \
               (self.username, self.password, self.email, self.phone_number, self.instrument, self.genre)


class Band(Base):
    __tablename__ = "bands"
    id = Column(Integer, primary_key=True)
    name = Column(String)
    genre = Column(String)
    admin_id = Column(Integer)

    def __init__(self, name, genre, admin_id):
        self.name = name
        self.genre = genre
        self.admin_id = admin_id

    def __repr__(self):
        if self.id:
            return "<Band(id=%d, name='%s', genre='%s')>" % (self.id, self.name, self.genre)
        return "<Band(name='%s', genre='%s')>" % (self.name, self.genre)


class UserBand(Base):
    __tablename__ = "userband"
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer)
    band_id = Column(Integer)
    declined = Column(Boolean)

    def __init__(self, user_id, band_id, declined=False):
        self.user_id = user_id
        self.band_id = band_id
        self.declined = declined

    def __repr__(self):
        return "UserBand { user : %d , band : %d }" % (self.user_id, self.band_id)


class BandInstrument(Base):
    __tablename__ = 'bandinstruement'
    id = Column(Integer, primary_key=True)
    band_id = Column(Integer)
    instrument = Column(String)
    count = Column(Integer)

    def __init__(self, band_id, instrument, count):
        self.band_id = band_id
        self.instrument = instrument
        self.count = count

    def __repr__(self):
        return "BandInstrument: <bandID:%s, instrument:%s, count:%s" % (self.band_id, self.instrument, self.count)


if __name__ == '__main__':
    Base.metadata.create_all(engine)
