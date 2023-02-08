# library & dataset
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.pyplot as axis

from matplotlib.backends.backend_pdf import PdfPages
from datetime import datetime

import collections 
import json
import urllib
import sys

import time

traffic_light_dict = { 
                       "SEN-TRAF-01-C1" : "C. Fermín Caballero (C1) - C. Cervantes",
                       "SEN-TRAF-01-C2" : "C. Fermín Caballero (C2) - C. Hurtado de Mendoza",
                       "SEN-TRAF-01-C3" : "C. Fermín Caballero (C3) - C. Diego Jiménez",
                       "SEN-TRAF-02"    : "C. Ramón y Cajal - C. Las Torres",
                       "SEN-TRAF-03"    : "C. Camino de Cañete - Av. Mediterráneo",
                       "SEN-TRAF-04"    : "C. Cervantes - Av. Castilla La Mancha",
                       "SEN-TRAF-06"    : "Av. República Argentina - C. Cervantes",
                       "SEN-TRAF-07"    : "Av. República Argentina (1) - A-40 (Sentido Saliente)",
                       "SEN-TRAF-08-C1" : "Av. República Argentina (16) - C. Fermín Caballero (C1)",
                       "SEN-TRAF-08-C2" : "Av. República Argentina (16) - C. Fermín Caballero (C2)",
                       "SEN-TRAF-10"    : "Av. Castilla La Mancha - Av. de los Reyes Católicos",
                       "SEN-TRAF-11-C1" : "Av. Castilla La Mancha - C. Fermín Caballero",
                       "SEN-TRAF-11-C2" : "Av. Castilla La Mancha - Av. República Argentina",
                       "SEN-TRAF-12-C1" : "C. Aguirre - Pl. Hispanidad",
                       "SEN-TRAF-12-C2" : "C. Aguirre - C. Noheda",
                       "SEN-TRAF-13"    : "C. Noheda - C. Aguirre",
                       "SEN-TRAF-14-C1" : "C. Noheda - C. Las Torres",
                       "SEN-TRAF-14-C2" : "C. Aguirre - C. Dr. Ferrán",
                       "SEN-TRAF-15"    : "C. Juan Correcher - C. Parque San Julian",
                       "SEN-TRAF-19"    : "C. Tintes - C. Las Torres",
                       "SEN-TRAF-20"    : "C. Colón (28) - Av. San Ignacio de Loyola",
                       "SEN-TRAF-22-C1" : "C. Calderón de la Barca - C. Fray Luis de León",
                       "SEN-TRAF-22-C2" : "C. Calderón de la Barca - Av. Virgen de la Luz",
                       "SEN-TRAF-23-C1" : "C. Colón - Av. San Ignacio de Loyola",
                       "SEN-TRAF-23-C2" : "C. Colón - Av. Virgen de la Luz",
                       "SEN-TRAF-24-C1" : "Av. San Ignacio de Loyola - C. Sargal",
                       "SEN-TRAF-24-C2" : "Av. San Ignacio de Loyola - A-40 (Sentido Saliente)",
                       "SEN-TRAF-28"    : "C. Colón - Av. San Ignacio de Loyola",
                       "SEN-TRAF-29"    : "C. Fermín Caballero - C. de los Hermanos Becerril",
                       "SEN-TRAF-30"    : "C. Parque San Julian - C. Aguirre",
                       "SEN-TRAF-31"    : "C. Las Torres - C. Aguirre",
                       "SEN-TRAF-32-C1" : "Av. San Ignacio de Loyola - Puente Virgen de la Luz",
                       "SEN-TRAF-32-C2" : "Av. San Ignacio de Loyola - Av. República Argentina",
                       "SEN-TRAF-34-C1" : "N-320 (Sentido Entrante -> Cuenca) - C. de los Hermanos Becerril",
                       "SEN-TRAF-34-C2" : "C. de los Hermanos Becerril - N-320 (Sentido Saliente -> Valencia)",
                       "SEN-TRAF-35-C1" : "N-400 (Sentido Entrante -> Cuenca) - Av. de los Reyes Católicos",
                       "SEN-TRAF-35-C2" : "N-400 (Sentido Entrante -> Cuenca) - Poligono La Cerrajera"
                     }
    

url_pattern = 'https://datosabiertos.cuenca.es/api/3/action/datastore_search?resource_id=74a58122-630d-49ef-a160-af53a0add1bb&limit=2000&q=#DATE#'

def download_day_record(date, sensor_id):
    
    query_url = url_pattern.replace('#DATE#', date)
    
    fileobj = urllib.request.urlopen(query_url)
    
    json_response =  json.loads(fileobj.read().decode('utf-8'))
    
    json_response = json_response['result']
    
    json_response = json_response['records']

    light_vehicles_record = {}
    timestamp = ''
    
    for record in json_response:
        
        if record['nombre'] == sensor_id:
            timestamp = record['fecha'].split('T')
            
            if timestamp[0] == date:
                light_vehicles_record[timestamp[1]] = record['ligeros']
    
    
    light_vehicles_record = collections.OrderedDict(sorted(light_vehicles_record.items()))
    
    return dict(light_vehicles_record)

#End of def download_day_record(date, sensor_id)

def analyze_json_record(sensor_id, input_path):
    
    f = open(input_path, 'r').read()

    records = json.loads(f)

    records = records['records']

    light_vehicles_record = {}
    timestamp = ''
    timestamp_hour = ''

    for record in records:
        if record['sensor_id'] == sensor_id:
            timestamp = record['timestamp'].split(' ')
            
            input_timestamp = input_path.replace('./', '').replace('-record.json', '')
            
            if timestamp[0] == input_timestamp:
                timestamp_hour = timestamp[1].split(':')
                light_vehicles_record[timestamp[1]] = record['light_vehicles']


    light_vehicles_record = collections.OrderedDict(sorted(light_vehicles_record.items()))

    light_vehicles_record = dict(light_vehicles_record)
    
    output_figure = plt.figure(figsize=(20,20))
    
    # Create bars
    plt.bar(range(len(light_vehicles_record)), list(light_vehicles_record.values()), align='center')

    # Create names on the x-axis
    plt.xticks(range(len(light_vehicles_record)), list(light_vehicles_record.keys()))
    
    # Rotate x labels 
    plt.xticks(rotation=90)
    
    plt.xlabel("Numero de vehículos por hora", fontdict=label_font)
    
    plt.ylabel("Distribución Horaria", fontdict=label_font)
    
    title = str(timestamp[0]) + ' - Record Analysis'
    plt.title(title, fontdict=title_font)
    
    # Tweak spacing to prevent clipping of ylabel
    # plt.subplots_adjust(left=0.2, right=0.8, bottom=0.2, top=0.8)

    # Show graphic
    # plt.show()
    
    return output_figure

# End of def analyze_json_record(sensor_id, input_path)

input_record_path_patern = "./Input_Files/#DATE#-record.json"

def analyze_month_record(sensor_id, output_path, database_type):
    
    date_format = '2022-05-#DAY#'
    
    pdf_file = PdfPages(output_path)
    
    firstPage = plt.figure(figsize=(12,8))
    firstPage.clf()
    txt = 'Traffic Record Analysis: \n May - 2022 \n' + traffic_light_dict[sensor_id] + '\n' + sensor_id
    firstPage.text(0.5,0.5, txt, size=24, ha="center")
    txt = 'Automatically generated from data obtained from: \n https://datosabiertos.cuenca.es/dataset/sensores-de-trafico/resource/74a58122-630d-49ef-a160-af53a0add1bb'
    firstPage.text(0.5,0.25, txt, size=12, ha="center")
    txt = 'Developed by JaySpazio (aka Juan Morales Sáez)'
    firstPage.text(0.5,0.10, txt, size=12, ha="center")
    pdf_file.savefig()
    plt.close(firstPage)
    
    actual_day = int(datetime.now().strftime("%d"))
    
    record_day = 1
    
    while record_day < actual_day:
        
        date = date_format.replace('#DAY#', str(record_day).zfill(2))
        
        if database_type == 'local':
            light_vehicles_record = download_day_record(date, sensor_id)
            output_plot = create_record_figure(date, light_vehicles_record)
        else:
            input_path = input_record_path_patern.replace('#DATE#', date)
            output_plot = analyze_json_record(sensor_id, input_path)
        
        pdf_file.savefig(output_plot)
        
        plt.close(output_plot)
        
        record_day = record_day + 1
        
     # We can also set the file's metadata via the PdfPages object:
    pdf_dict = pdf_file.infodict()
    pdf_dict['Title'] = 'Traffic Record Analysis - May-2022'
    pdf_dict['Author'] = 'JaySpazio (aka Juan Morales Sáez)'
    pdf_dict['Subject'] = 'Traffic Record Analysis - May-2022 ' + traffic_light_dict[sensor_id]
    pdf_dict['Keywords'] = 'Traffic Record Analysis'
    pdf_dict['CreationDate'] = datetime.today()
    pdf_dict['ModDate'] = datetime.today()
        
    pdf_file.close()

# End of def analyze_month_record(sensor_id, output_path)

label_font = {'size': 20}
title_font = {'size': 40}

weekDays = ["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"]

def create_record_figure(date, light_vehicles_record):
    
    output_figure = plt.figure(figsize=(20,20))
    
    # Create bars
    plt.bar(range(len(light_vehicles_record)), list(light_vehicles_record.values()), align='center')

    # Create names on the x-axis
    plt.xticks(range(len(light_vehicles_record)), list(light_vehicles_record.keys()))
    
    # Rotate x labels 
    plt.xticks(rotation=90)
    
    plt.xlabel("Numero de vehículos por hora", fontdict=label_font)
    
    plt.ylabel("Distribución Horaria", fontdict=label_font)
    
    day_of_week = datetime.strptime(date, '%Y-%m-%d').weekday()

    title = weekDays[day_of_week] + ' ' + str(date) + ' - Record Analysis'
    plt.title(title, fontdict=title_font)

# End of def create_record_figure(date, light_vehicles_record)

output_path_patern = './Output_Files/Local/#SENSOR_ID#-#MONTH_DATE#-records-analysis.pdf'

def main() -> int:
    
    start_time = time.time()
    
    database_type = 'local'
          
    for sensor_id in traffic_light_dict.keys():
    
        output_path = output_path_patern.replace('#SENSOR_ID#', sensor_id).replace('#MONTH_DATE#', '2022-05')

        analyze_month_record(sensor_id, output_path, database_type)

    print('Finished\n')
    
    print('Processing time: ' + str(time.time()-start_time) + ' s')

    return 0

# End of def main() -> int

if __name__ == '__main__':
    sys.exit(main())  # next section explains the use of sys.exit