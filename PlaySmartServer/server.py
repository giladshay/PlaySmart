"""
    server file
    Crated by: Gil-Ad Shay
    Last edited: 02.05.2020
    Version: 'with documentation'
"""
from flask import Flask, request, render_template
from sqlalchemy.orm import sessionmaker
from database.band import create as create_band
from database.band import get as get_band
from database.bandinstrument import create as create_band_instrument
from database.bandinstrument import get as get_band_instrument
from database.database import engine, User, Band, UserBand, BandInstrument
from database.user import create as create_user
from database.user import delete as delete_user
from database.user import get as get_user
from database.userband import create as create_userband
from database.userband import get as get_userband

app = Flask(__name__)


def convert_to_id_list(lst):

    id_list = []
    for obj in lst:
        id_list.append(obj.id)
    return id_list


@app.route("/", methods=['POST'])
def index():
    """
        Home Page
        :return the bands that the user can join into as JSON
    """
    try:
        s = sessionmaker(bind=engine)
        s = s()
        data = request.get_json()
        u = get_user.by_username(s, data["username"])
        users_in_band = []
        offers = []
        users_id = []

        # filter the bands
        for band in get_band.all(s):
            users_band = get_userband.users_by_band(s, band.id)
            for userband in users_band:
                users_id.append(userband.user_id)
            if u.id not in users_id and band.genre == u.genre: # user is not in band and band's genre is the user's genre
                # if user is not in that band already
                current_instruments = get_userband.users_by_band_and_instrument(s, band.id, u.instrument)
                    count_instruments = get_band_instrument.count_instrument_in_band(s, band.id, u.instrument)
                if count_instruments > 0 and current_instruments < count_instruments:
                    for userband in get_userband.users_by_band(s, band.id):
                        user = get_user.by_id(s, userband.user_id)
                        users_in_band.append(
                            {"name": user.username, "instrument": user.instrument})  # create list of users in band
                    offers.append(
                        {'name': band.name, 'genre': band.genre, 'admin': get_user.by_id(s, band.admin_id).username,
                         'users': users_in_band})  # append the offer
        print(offers)
        return {"Status": "OK", "Method": "Index", "Offers": offers}  # return the information
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "Index", "Detail": str(e)}


@app.route("/get_band_info", methods=['POST'])
def get_band_info():
    """
    :return: the band information as JSON
    """
    try:
        s = sessionmaker(bind=engine)
        s = s()
        data = request.get_json()
        band = get_band.by_bandname(s, data["name"])
        instruments = {}
        for band_instrument in get_band_instrument.by_band(s, get_band.by_bandname(s, bandname=band.name).id):
            instruments[band_instrument.instrument] = band_instrument.count
        return {"Status": "OK", "Method": "GetBandInfo",
                "band": {"id": band.id, "name": band.name, "genre": band.genre}, "instruments": instruments}
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "GetBandInfo", "Detail": str(e)}


@app.route("/accept", methods=['POST'])
def accept_joining_band():
    """
    :return: the Username and Bandname
    """
    try:
        s = sessionmaker(bind=engine)
        s = s()
        data = request.get_json()
        user = get_user.by_username(s, data["username"])
        band = get_band.by_bandname(s, data["bandname"])
        userband = UserBand(user_id=user.id, band_id=band.id, declined=False)
        create_userband.create(s, userband)
        return {"Status": "OK", "Method": "Accept",
                "Info": {"user": get_user.to_json(user), "band": get_band.to_json(band)}}
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "Accept", "Detail": str(e)}


@app.route("/decline", methods=['POST'])
def decline_joining_band():
    """
    :return: {"Status": 'Status', "Method": "Decline", "Info": {"user": 'username', "band": 'bandname'}
    """
    try:
        s = sessionmaker(bind=engine)
        s = s()
        data = request.get_json()
        userband = UserBand(get_user.by_username(s, data["username"]).id, get_band.by_bandname(s, data["bandname"]).id,
                            True)
        create_userband.create(s, userband)
        return {"Status": "OK", "Method": "Decline", "Info": {"user": data["username"], "band": data["bandname"]}}
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "Decline", "Detail": str(e)}


@app.route("/login", methods=['POST'])
def login():
    """
    :return: {"Status": 'Status', "Method": "Login",
                        "User": {"ID": id, "Username": 'username', "Email": 'email', "Number": 'number',
                                 "Instrument": 'instrument', "Genre": 'genre', "Bands": lst(Band)}}
    """
    data = request.get_json()
    print(data)
    try:
        s = sessionmaker(bind=engine)
        s = s()
        u = get_user.by_username(s, data["username"])
        if u:
            u_bands = get_band.convert_userband_list(s, get_userband.bands_by_user(s, u.id))  # get all the user's bands
            if u.password == data["password"]:
                return {"Status": "OK", "Method": "Login",
                        "User": {"ID": u.id, "Username": u.username, "Email": u.email,
                                 "Number": u.phone_number,
                                 "Instrument": u.instrument,
                                 "Genre": u.genre,
                                 "Bands": u_bands}}
            else:
                return {"Status": "NOT OK", "Method": "Login", "Detail": "USERNAME AND PASSWORD ARE NOT MATCHED"}
        else:
            return {"Status": "NOT OK", "Method": "Login", "Detail": "NO SUCH USER"}
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "Login", "Detail": str(e)}


@app.route("/register", methods=['POST'])
def register():
    """
    :return: {"Status": 'Status', "Method": "Register", "User": {"ID": id}}
    """
    s = sessionmaker(bind=engine)
    s = s()
    data = request.get_json()
    try:
        print(data)
        u = User(username=data["username"], password=data["password"], email=data["email"],
                 phone_number=data["number"], instrument=data["instrument"], genre=data["genre"])
        create_user.create(s, u)
        print(u)
        return {"Status": "OK", "Method": "Register", "User": {"ID": u.id}}
    except Exception as e:
        delete_user.delete(s, get_user.by_username(s, data["username"]))
        print(e)
        return {"Status": "NOT OK", "Method": "Register", "Detail": str(e)}


@app.route("/new_band", methods=['POST'])
def new_band():
    """
    :return: {"Status": 'Status', "Method": "New Band", "Band": {"name": 'name', "genre": 'genre', "instruments": lst(Instrument)}}
    """
    try:
        s = sessionmaker(bind=engine)
        s = s()
        data = request.get_json()
        print(data)
        print(data["bandInfo"])
        print(data["instruments"])

        user = get_user.by_username(s, data["bandInfo"]["adminUser"])
        band = Band(name=data["bandInfo"]["name"], genre=data["bandInfo"]["genre"], admin_id=user.id)
        create_band.create(s, band)
        userband = UserBand(user_id=user.id, band_id=band.id)
        create_userband.create(s, userband)
        for instrument in data["instruments"]:
            bi = BandInstrument(band_id=band.id, instrument=instrument, count=data["instruments"][instrument])
            create_band_instrument.create(s, bi)

        return {"Status": "OK", "Method": "New Band",
                "Band": {"name": band.name, "genre": band.genre,
                         "instruments": get_band_instrument.instruments(s, band.id)}}
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "New Band", "Detail": str(e)}


@app.route("/bands", methods=['POST'])
def bands():
    """
    :return: {"Status": 'Status', "Method": "GetBands", "bands": lst(Band)}
    """
    try:
        s = sessionmaker(bind=engine)
        s = s()
        data = request.get_json()
        print(data)
        user = get_user.by_username(s, data["username"])
        print(user)
        bands = []
        for userband in get_userband.bands_by_user(s, user_id=user.id):
            bands.append(get_band.to_json(band=get_band.by_id(s, userband.band_id)))
        return {"Status": "OK", "Method": "GetBands", "bands": bands}
    except Exception as e:
        print(e)
        return {"Status": "NOT OK", "Method": "GetBands", "Detail": str(e)}


if __name__ == '__main__':
    app.secret_key = 'secret_key'
    app.run(debug=True, port=5000)
