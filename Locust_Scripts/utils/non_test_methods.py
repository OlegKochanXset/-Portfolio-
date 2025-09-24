import csv, random
from datetime import datetime, timedelta
from urllib.parse import unquote_plus

def open_csv_field(filepath=str):
    with open(filepath, 'r') as file:
        reader = csv.DictReader(file)
        return list(reader)

    #                                                       Генерирует даты для теста.

def generationFligthsDates():
    dates_list = {}

    dates_list["depart_Date"] = unquote_plus((datetime.now() + timedelta(days=random.randrange(1,5))).strftime("%m/%d/%Y"))
    dates_list["arrive_Date"] = unquote_plus((datetime.now() + timedelta(days=random.randrange(10, 20))).strftime("%m/%d/%Y"))

    return dates_list


def processCancelRequestBody(ids_list=list, cg_list=list):
    long_flightsID = ''
    long_cgifields = ''

    count = len(ids_list)

    for i in range(count):
        long_flightsID = long_flightsID + f"&flightID={ids_list[i]}"
        long_cgifields = long_cgifields + f"&.cgifields={cg_list[i]}"

    done_body_cancel = f'1=on{long_flightsID}&removeFlights.x=49&removeFlights.y=9{long_cgifields}'

    return done_body_cancel

