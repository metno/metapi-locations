/*
    MET-API

    Copyright (C) 2014 met.no
    Contact information:
    Norwegian Meteorological Institute
    Box 43 Blindern
    0313 OSLO
    NORWAY
    E-mail: met-api@met.no

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
    MA 02110-1301, USA
*/

package no.met.fileutil

import java.io.File
import java.util.zip._
import scala.util.matching._
import scala.util.{ Try, Success, Failure }
import scala.collection.JavaConversions._
import java.nio.file.{ Path, FileSystems }
import java.io.Reader
import java.io.InputStream
import java.io.InputStreamReader

class Zip(val zipFile: Path) {
  val zip = new ZipFile(zipFile.toFile)

  def getEntries(which: Regex): Seq[ZipEntry] = {
    def useThis(file: String) = file match {
      case which(_*) => true
      case _ => false
    }
    val es = for (e <- zip.entries if !e.isDirectory && useThis(e.getName)) yield e
    es.toSeq
  }

  def getReader(e: ZipEntry): Try[Reader] = Try {
    new InputStreamReader(zip.getInputStream(e))
  }
}

object Zip {
  def apply(zip: Path): Try[Zip] = Try { new Zip(zip) }
  def apply(zip: File): Try[Zip] = Try { new Zip(zip.toPath) }
  def apply(zip: String): Try[Zip] = Try { new Zip(FileSystems.getDefault().getPath(zip)) }
}
