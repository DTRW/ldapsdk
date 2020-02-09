/*
 * Copyright 2019-2020 Ping Identity Corporation
 * All Rights Reserved.
 */
/*
 * Copyright (C) 2019-2020 Ping Identity Corporation
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
package com.unboundid.ldap.sdk.unboundidds.controls;



import java.util.ArrayList;

import com.unboundid.asn1.ASN1Boolean;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.asn1.ASN1Long;
import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Sequence;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DecodeableControl;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Debug;
import com.unboundid.util.NotMutable;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.ThreadSafetyLevel;

import static com.unboundid.ldap.sdk.unboundidds.controls.ControlMessages.*;



/**
 * This class provides a response control that may be used to convey the
 * password (and other associated information) generated in response to a
 * {@link GeneratePasswordRequestControl}.
 * <BR>
 * <BLOCKQUOTE>
 *   <B>NOTE:</B>  This class, and other classes within the
 *   {@code com.unboundid.ldap.sdk.unboundidds} package structure, are only
 *   supported for use against Ping Identity, UnboundID, and
 *   Nokia/Alcatel-Lucent 8661 server products.  These classes provide support
 *   for proprietary functionality or for external specifications that are not
 *   considered stable or mature enough to be guaranteed to work in an
 *   interoperable way with other types of LDAP servers.
 * </BLOCKQUOTE>
 * <BR>
 * This control has an OID of "1.3.6.1.4.1.30221.2.5.59", a criticality of
 * false, and a value with the following encoding:
 * <PRE>
 *   GeneratePasswordResponse ::= SEQUENCE {
 *        generatedPassword          OCTET STRING,
 *        mustChangePassword         BOOLEAN,
 *        secondsUntilExpiration     [0] INTEGER OPTIONAL,
 *        ... }
 * </PRE>
 *
 * @see  GeneratePasswordRequestControl
 */
@NotMutable()
@ThreadSafety(level=ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class GeneratePasswordResponseControl
       extends Control
       implements DecodeableControl
{
  /**
   * The OID (1.3.6.1.4.1.30221.2.5.59) for the generate password response
   * control.
   */
  public static final String GENERATE_PASSWORD_RESPONSE_OID =
       "1.3.6.1.4.1.30221.2.5.59";



  /**
   * The BER type for the {@code secondsUntilExpiration} element.
   */
  private static final byte TYPE_SECONDS_UNTIL_EXPIRATION = (byte) 0x80;



  /**
   * The serial version UID for this serializable class.
   */
  private static final long serialVersionUID = 7542512192838228238L;



  // The generated password included in the control.
  private final ASN1OctetString generatedPassword;

  // Indicates whether the user will be required to choose a new password the
  // first time they authenticate.
  private final boolean mustChangePassword;

  // The number of seconds until the new password will expire.
  private final Long secondsUntilExpiration;



  /**
   * Creates a new empty control instance that is intended to be used only for
   * decoding controls via the {@code DecodeableControl} interface.
   */
  GeneratePasswordResponseControl()
  {
    generatedPassword = null;
    mustChangePassword = false;
    secondsUntilExpiration = null;
  }



  /**
   * Creates a new generate password response control with the provided
   * information.
   *
   * @param  generatedPassword       The password generated by the server.  It
   *                                 must not be {@code null}.
   * @param  mustChangePassword      Indicates whether the user will be required
   *                                 to choose a new password the first time
   *                                 they authenticate.
   * @param  secondsUntilExpiration  The number of seconds until the new
   *                                 password will expire.  It may be
   *                                 {@code null} if the new password will not
   *                                 expire.
   */
  public GeneratePasswordResponseControl(final String generatedPassword,
                                         final boolean mustChangePassword,
                                         final Long secondsUntilExpiration)
  {
    this(new ASN1OctetString(generatedPassword), mustChangePassword,
         secondsUntilExpiration);
  }



  /**
   * Creates a new generate password response control with the provided
   * information.
   *
   * @param  generatedPassword       The password generated by the server.  It
   *                                 must not be {@code null}.
   * @param  mustChangePassword      Indicates whether the user will be required
   *                                 to choose a new password the first time
   *                                 they authenticate.
   * @param  secondsUntilExpiration  The number of seconds until the new
   *                                 password will expire.  It may be
   *                                 {@code null} if the new password will not
   *                                 expire.
   */
  public GeneratePasswordResponseControl(final byte[] generatedPassword,
                                         final boolean mustChangePassword,
                                         final Long secondsUntilExpiration)
  {
    this(new ASN1OctetString(generatedPassword), mustChangePassword,
         secondsUntilExpiration);
  }



  /**
   * Creates a new generate password response control with the provided
   * information.
   *
   * @param  generatedPassword       The password generated by the server.  It
   *                                 must not be {@code null}.
   * @param  mustChangePassword      Indicates whether the user will be required
   *                                 to choose a new password the first time
   *                                 they authenticate.
   * @param  secondsUntilExpiration  The number of seconds until the new
   *                                 password will expire.  It may be
   *                                 {@code null} if the new password will not
   *                                 expire.
   */
  private GeneratePasswordResponseControl(
               final ASN1OctetString generatedPassword,
               final boolean mustChangePassword,
               final Long secondsUntilExpiration)
  {
    super(GENERATE_PASSWORD_RESPONSE_OID, false,
         encodeValue(generatedPassword, mustChangePassword,
              secondsUntilExpiration));

    this.generatedPassword = generatedPassword;
    this.mustChangePassword = mustChangePassword;
    this.secondsUntilExpiration = secondsUntilExpiration;
  }



  /**
   * Creates a new generate password response control with the provided
   * information.
   *
   * @param  oid         The OID for the control.
   * @param  isCritical  Indicates whether the control should be marked
   *                     critical.
   * @param  value       The encoded value for the control.  This may be
   *                     {@code null} if no value was provided.
   *
   * @throws  LDAPException  If the provided control cannot be decoded as a
   *                         generate password response control.
   */
  public GeneratePasswordResponseControl(final String oid,
                                         final boolean isCritical,
                                         final ASN1OctetString value)
         throws LDAPException
  {
    super(oid, isCritical,  value);

    if (value == null)
    {
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_GENERATE_PASSWORD_RESPONSE_NO_VALUE.get());
    }

    try
    {
      final ASN1Element valElement = ASN1Element.decode(value.getValue());
      final ASN1Element[] elements =
           ASN1Sequence.decodeAsSequence(valElement).elements();
      generatedPassword = ASN1OctetString.decodeAsOctetString(elements[0]);
      mustChangePassword =
           ASN1Boolean.decodeAsBoolean(elements[1]).booleanValue();

      Long secsUntilExp = null;
      for (int i=2; i < elements.length; i++)
      {
        final ASN1Element e = elements[i];
        switch (e.getType())
        {
          case TYPE_SECONDS_UNTIL_EXPIRATION:
            secsUntilExp = ASN1Long.decodeAsLong(e).longValue();
            break;
          default:
            // This is a field we don't currently recognize but might be defined
            // in the future.
            break;
        }
      }

      secondsUntilExpiration = secsUntilExp;
    }
    catch (final Exception e)
    {
      Debug.debugException(e);
      throw new LDAPException(ResultCode.DECODING_ERROR,
           ERR_GENERATE_PASSWORD_RESPONSE_CANNOT_DECODE_VALUE.get(
                StaticUtils.getExceptionMessage(e)),
           e);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public GeneratePasswordResponseControl decodeControl(final String oid,
                                              final boolean isCritical,
                                              final ASN1OctetString value)
         throws LDAPException
  {
    return new GeneratePasswordResponseControl(oid, isCritical, value);
  }



  /**
   * Extracts a generate password  response control from the provided result.
   *
   * @param  result  The result from which to retrieve the generate password
   *                 response control.
   *
   * @return  The generate password response control contained in the provided
   *          result, or {@code null} if the result did not contain a generate
   *          password response control.
   *
   * @throws  LDAPException  If a problem is encountered while attempting to
   *                         decode the generate password response control
   *                         contained in the provided result.
   */
  public static GeneratePasswordResponseControl get(final LDAPResult result)
         throws LDAPException
  {
    final Control c = result.getResponseControl(GENERATE_PASSWORD_RESPONSE_OID);
    if (c == null)
    {
      return null;
    }

    if (c instanceof GeneratePasswordResponseControl)
    {
      return (GeneratePasswordResponseControl) c;
    }
    else
    {
      return new GeneratePasswordResponseControl(c.getOID(), c.isCritical(),
           c.getValue());
    }
  }



  /**
   * Encodes the provided information appropriately for use as the value of this
   * control.
   *
   * @param  generatedPassword        The password generated by the server.  It
   *                                 must not be {@code null}.
   * @param  mustChangePassword      Indicates whether the user will be required
   *                                 to choose a new password the first time
   *                                 they authenticate.
   * @param  secondsUntilExpiration  The number of seconds until the new
   *                                 password will expire.  It may be
   *                                 {@code null} if the new password will not
   *                                 expire.
   *
   * @return  The ASN.1 octet string suitable for use as the control value.
   */
  private static ASN1OctetString encodeValue(
                                      final ASN1OctetString generatedPassword,
                                      final boolean mustChangePassword,
                                      final Long secondsUntilExpiration)
  {
    final ArrayList<ASN1Element> elements = new ArrayList<>(3);
    elements.add(generatedPassword);
    elements.add(mustChangePassword
         ? ASN1Boolean.UNIVERSAL_BOOLEAN_TRUE_ELEMENT
         : ASN1Boolean.UNIVERSAL_BOOLEAN_FALSE_ELEMENT);

    if (secondsUntilExpiration != null)
    {
      elements.add(new ASN1Long(TYPE_SECONDS_UNTIL_EXPIRATION,
           secondsUntilExpiration));
    }

    return new ASN1OctetString(new ASN1Sequence(elements).encode());
  }



  /**
   * Retrieves the password that was generated by the server.
   *
   * @return  The password that was generated by the server.
   */
  public ASN1OctetString getGeneratedPassword()
  {
    return generatedPassword;
  }



  /**
   * Retrieves a string representation of the password that was generated by the
   * server.
   *
   * @return  A string representation of the password that was generated by the
   *          server.
   */
  public String getGeneratedPasswordString()
  {
    return generatedPassword.stringValue();
  }



  /**
   * Retrieves the bytes that comprise the password that was generated by the
   * server.
   *
   * @return  The bytes that comprise the password that was generated by the
   *          server.
   */
  public byte[] getGeneratedPasswordBytes()
  {
    return generatedPassword.getValue();
  }



  /**
   * Indicates whether the user will be required to change their password the
   * first time they authenticate.
   *
   * @return  {@code true} if the user will be required to change their password
   *          the first time they authenticate, or {@code false} if not.
   */
  public boolean mustChangePassword()
  {
    return mustChangePassword;
  }



  /**
   * Retrieves the length of time, in seconds, until the generated password will
   * expire.
   *
   * @return  The length of time, in seconds, until the generated password will
   *          expire, or {@code null} if this is not available (e.g., because
   *          the generated password will not expire).
   */
  public Long getSecondsUntilExpiration()
  {
    return secondsUntilExpiration;
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public String getControlName()
  {
    return INFO_CONTROL_NAME_GENERATE_PASSWORD_RESPONSE.get();
  }



  /**
   * {@inheritDoc}
   */
  @Override()
  public void toString(final StringBuilder buffer)
  {
    buffer.append("GeneratePasswordResponseControl(mustChangePassword=");
    buffer.append(mustChangePassword);

    if (secondsUntilExpiration != null)
    {
      buffer.append(", secondsUntilExpiration=");
      buffer.append(secondsUntilExpiration);
    }

    buffer.append(')');
  }
}