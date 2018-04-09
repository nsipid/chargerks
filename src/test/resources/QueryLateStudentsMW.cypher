MATCH (x1:Course)-[:matches*0..3]-()-[x2:Meet]->()-[:matches*0..3]-(x3:DaysOfWeek {referent: "MW"})
WITH DISTINCT x1, x3
MATCH (x4:Course)-[:matches*0..3]-()-[x5:Meet]->()-[:matches*0..3]-(x3)
WITH DISTINCT x1, x3, x4
MATCH (x6:Student)-[:matches*0..3]-()-[x7:enroll]->()-[:matches*0..3]-(x1)
WITH DISTINCT x1, x3, x4, x6
MATCH (x6)-[:matches*0..3]-()-[x8:enroll]->()-[:matches*0..3]-(x4)
WITH DISTINCT x1, x3, x4, x6
MATCH (x1)-[:matches*0..3]-()-[x9:End]->()-[:matches*0..3]-(x10:TimeOfDay)
WITH DISTINCT x1, x3, x4, x6, x10
MATCH (x4)-[:matches*0..3]-()-[x21:Start]->()-[:matches*0..3]-(x11:TimeOfDay)
WITH DISTINCT x1, x3, x4, x6, x10, x11
MATCH (x1)-[:matches*0..3]-()-[x12:Bldg]->()-[:matches*0..3]-(x13:Building)
WITH DISTINCT x1, x3, x4, x6, x10, x11, x13
MATCH (x4)-[:matches*0..3]-()-[x14:Bldg]->()-[:matches*0..3]-(x15:Building)
WITH DISTINCT x1, x3, x4, x6, x10, x11, x13, x15
MATCH (x16:Trip)-[:matches*0..3]-()-[x17:origin]->()-[:matches*0..3]-(x13)
WITH DISTINCT x1, x3, x4, x6, x10, x11, x13, x15, x16
MATCH (x16)-[:matches*0..3]-()-[x18:destination]->()-[:matches*0..3]-(x15)
WITH DISTINCT x1, x3, x4, x6, x10, x11, x13, x15, x16
MATCH (x16)-[:matches*0..3]-()-[x19:duration]->()-[:matches*0..3]-(x20:Seconds)
WITH DISTINCT x1.referent as x1r, x3.referent as x3r, x4.referent as x4r, x6.referent as x6r, x10.referent as x10r, x11.referent as x11r, x13.referent as x13r, x15.referent as x15r, x16.referent as x16r, x20.referent as x20r
WHERE x10r <> "" AND x11r <> "" AND x10r <> "TBA" AND x11r <> "TBA"
WITH *, apoc.date.parse(x11r, "s", "HH:mm") - apoc.date.parse(x10r, "s", "HH:mm") AS actor1
WHERE actor1 > 0 AND toInteger(x20r) > actor1
RETURN *;