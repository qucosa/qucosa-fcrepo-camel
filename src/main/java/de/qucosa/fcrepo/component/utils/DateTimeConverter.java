/*
 * Copyright 2018 Saxon State and University Library Dresden (SLUB)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.qucosa.fcrepo.component.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateTimeConverter {
    
    public static Timestamp timestampWithTimezone(String dateString) throws ParseException, DatatypeConfigurationException {
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateString);
        DateFormat  df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        GregorianCalendar gc = xmlGregorianCalendar.toGregorianCalendar();
        Date date = gc.getTime();
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }
    
    public static Timestamp timestampWithOUTTimezone(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        Date date = dateFormat.parse(dateString);
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }
    
    public static java.sql.Date sqlDate(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date utilDate = dateFormat.parse(dateString);
        java.sql.Date date = new java.sql.Date(utilDate.getTime());
        return date;
    }
    
    public static String sqlTimestampToString(Timestamp timestamp) {
        Date date = new Date();
        date.setTime(timestamp.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        return dateFormat.format(date);
    }
}
