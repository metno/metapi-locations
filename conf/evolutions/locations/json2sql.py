# Print out Insert Statements from Kartverkets GeoJson format.
#
# The GeoJson format:
#    {
#      "type":"Feature",
#      "properties":{
#        "skr_snskrstat":"G", // Status of the feature. Not all are allowed.
#        "enh_ssr_id":58065,
#        "for_kartid":"",
#        "for_regdato":19990929,
#        "skr_sndato":19620101,
#        "enh_snmynd":"SK",
#        "for_sist_endret_dt":20040817,
#        "enh_snspraak":"NO", // Language
#        "nty_gruppenr":2,
#        "enh_snavn":"Stalsbergtjernet",
#        "enh_komm":604,
#        "enh_ssrobj_id":58249,
#        "enh_sntystat":"H",  // Main name (H) or Alias 
#        "enh_navntype":32,  // Type of the feature
#        "for_snavn":"Stalsbergtjernet",
#        "kom_fylkesnr":6,
#        "kpr_tekst": "N50 Kartdata"
#      },
#      "geometry":{ // Coordinate of the feature
#        "type":"Point",
#        "coordinates":[9.681019,59.650856 ]
#      }
#    }
# For a detailed rundown of the different concepts, see
# http://www.kartverket.no/globalassets/standard/sosi-standarden-del-1-og-2/sosi-standarden/sosi-standarden-4.3/sosistedsnavn_4_3_20111005.pdf

import json,sys

obj=json.load(sys.stdin)

sys.stdout.write("# --- !Ups\n\n")

for i in range(0, 2500000):
   if (obj["features"][i]["properties"]["enh_sntystat"] == 'H' and obj["features"][i]["properties"]["enh_snspraak"] == 'NO' and (obj["features"][i]["properties"]["enh_navntype"] == 100 or obj["features"][i]["properties"]["enh_navntype"] == 101 or obj["features"][i]["properties"]["enh_navntype"] == 132) and (obj["features"][i]["properties"]["skr_snskrstat"] == 'G' or obj["features"][i]["properties"]["skr_snskrstat"] == 'S' or obj["features"][i]["properties"]["skr_snskrstat"] == 'V' or obj["features"][i]["properties"]["skr_snskrstat"] == 'P')): 
     sys.stdout.write("INSERT INTO locationFeature VALUES ('")
     sys.stdout.write(obj["features"][i]["properties"]["enh_snavn"].encode('utf-8'))
     sys.stdout.write("', '")
     sys.stdout.write(obj["features"][i]["properties"]["enh_snspraak"].encode('utf-8'))
     sys.stdout.write("', '")
     sys.stdout.write(obj["features"][i]["properties"]["enh_sntystat"].encode('utf-8'))
     sys.stdout.write("', '")
     sys.stdout.write(obj["features"][i]["properties"]["skr_snskrstat"].encode('utf-8'))
     sys.stdout.write("', ")
     sys.stdout.write(str(obj["features"][i]["properties"]["enh_navntype"]))
     sys.stdout.write(", st_geomfromtext('POINT(")
     sys.stdout.write(str(obj["features"][i]["geometry"]["coordinates"][0]))
     sys.stdout.write(" ")
     sys.stdout.write(str(obj["features"][i]["geometry"]["coordinates"][1]))
     sys.stdout.write(")',4326));")
     sys.stdout.write('\n')
     sys.stdout.flush

sys.stdout.write("\n# --- !Downs\n\n")   
sys.stdout.write("DELETE FROM locationFeature;\n")
sys.stdout.flush