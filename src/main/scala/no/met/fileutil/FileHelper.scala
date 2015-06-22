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

import java.io.{ File, Reader, FileReader }
import java.nio.file.{ Files, FileSystems, Path }
import scala.util.{ Try, Failure, Success }
import scala.util.matching.Regex

trait FileHelper {
  def foreach(f: Reader => Unit): Unit
}

case class ordinaryFile(file: File) extends FileHelper {
  val r = Try(new FileReader(file))
  def foreach(f: Reader => Unit): Unit = r foreach { f(_) }
}

case class zipFile(zip: Zip, which: Regex) extends FileHelper {
  val r = zip.getEntries(which)
  def foreach(f: Reader => Unit): Unit = r foreach { zip.getReader(_) foreach { f(_) } }
}

object FileHelper {
  private def isZipFile(file: String) = file.toLowerCase.endsWith(".zip")

  def fileIter(file: String, which: Regex): Try[FileHelper] = Try {
    if (isZipFile(file)) {
      Zip(file) match {
        case Success(zip) => zipFile(zip, which)
        case Failure(e) => throw e
      }
    } else {
      file match {
        case which(_*) =>
          val p = FileSystems.getDefault().getPath(file)
          if (Files.exists(p) && Files.isReadable(p)) {
            ordinaryFile(p.toFile())
          } else {
            throw new Exception(s"The file '$file' do not exist or is not a regular file.")
          }
        case _ => throw new Exception(s"The filename '${file.toString}' do not match the regex '$which'")
      }
    }
  }
}
