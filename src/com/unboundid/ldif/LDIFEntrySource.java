/*
 * Copyright 2009-2010 UnboundID Corp.
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2009-2010 UnboundID Corp.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */
package com.unboundid.ldif;



import java.util.concurrent.atomic.AtomicBoolean;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.EntrySource;
import com.unboundid.ldap.sdk.EntrySourceException;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.util.Debug.*;
import static com.unboundid.util.Validator.*;



/**
 * This class provides an {@link EntrySource} that will read entries from an
 * LDIF file.
 * <BR><BR>
 * <H2>Example</H2>
 * The following example demonstrates the process that may be used for iterating
 * through all entries in an LDIF file using the entry source API:
 * <PRE>
 *   LDIFEntrySource entrySource =
 *        new LDIFEntrySource(new LDIFReader(pathToLDIFFile));
 *
 *   try
 *   {
 *     while (true)
 *     {
 *       try
 *       {
 *         Entry entry = entrySource.nextEntry();
 *         if (entry == null)
 *         {
 *           // There are no more entries to be read.
 *           break;
 *         }
 *         else
 *           {
 *           // Do something with the entry here.
 *         }
 *       }
 *       catch (EntrySourceException e)
 *       {
 *         // Some kind of problem was encountered (e.g., a malformed entry
 *         // found in the LDIF file, or an I/O error when trying to read).  See
 *         // if we can continue reading entries.
 *         if (! e.mayContinueReading())
 *         {
 *           break;
 *         }
 *       }
 *     }
 *   }
 *   finally
 *   {
 *     entrySource.close();
 *   }
 * </PRE>
 */
@ThreadSafety(level=ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDIFEntrySource
       extends EntrySource
{
  // Indicates whether this entry source has been closed.
  private final AtomicBoolean closed;

  // The LDIF reader from which entries will be read.
  private final LDIFReader ldifReader;



  /**
   * Creates a new LDAP entry source that will obtain entries from the provided
   * LDIF reader.
   *
   * @param  ldifReader  The LDIF reader from which to read entries.  It must
   *                     not be {@code null}.
   */
  public LDIFEntrySource(final LDIFReader ldifReader)
  {
    ensureNotNull(ldifReader);

    this.ldifReader = ldifReader;

    closed = new AtomicBoolean(false);
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public Entry nextEntry()
         throws EntrySourceException
  {
    if (closed.get())
    {
      return null;
    }

    try
    {
      final Entry e = ldifReader.readEntry();
      if (e == null)
      {
        close();
      }

      return e;
    }
    catch (LDIFException le)
    {
      debugException(le);
      if (le.mayContinueReading())
      {
        throw new EntrySourceException(true, le);
      }
      else
      {
        close();
        throw new EntrySourceException(false, le);
      }
    }
    catch (Exception e)
    {
      debugException(e);
      close();
      throw new EntrySourceException(false, e);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void close()
  {
    if (closed.compareAndSet(false, true))
    {
      try
      {
        ldifReader.close();
      }
      catch (Exception e)
      {
        debugException(e);
      }
    }
  }
}
