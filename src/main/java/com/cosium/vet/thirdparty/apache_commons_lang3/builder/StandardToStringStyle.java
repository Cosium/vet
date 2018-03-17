/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cosium.vet.thirdparty.apache_commons_lang3.builder;

/**
 * Works with {@link ToStringBuilder} to create a <code>toString</code>.
 *
 * <p>This class is intended to be used as a singleton. There is no need to instantiate a new style
 * each time. Simply instantiate the class once, customize the values as required, and store the
 * result in a public static final variable for the rest of the program to access.
 *
 * @since 1.0
 */
public class StandardToStringStyle extends ToStringStyle {

  /**
   * Required for serialization support.
   *
   * @see java.io.Serializable
   */
  private static final long serialVersionUID = 1L;

  /** Constructor. */
  public StandardToStringStyle() {
    super();
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use the class name.
   *
   * @return the current useClassName flag
   */
  @Override
  public boolean isUseClassName() { // NOPMD as this is implementing the abstract class
    return super.isUseClassName();
  }

  /**
   * Sets whether to use the class name.
   *
   * @param useClassName the new useClassName flag
   */
  @Override
  public void setUseClassName(
      final boolean useClassName) { // NOPMD as this is implementing the abstract class
    super.setUseClassName(useClassName);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to output short or long class names.
   *
   * @return the current useShortClassName flag
   * @since 2.0
   */
  @Override
  public boolean isUseShortClassName() { // NOPMD as this is implementing the abstract class
    return super.isUseShortClassName();
  }

  /**
   * Sets whether to output short or long class names.
   *
   * @param useShortClassName the new useShortClassName flag
   * @since 2.0
   */
  @Override
  public void setUseShortClassName(
      final boolean useShortClassName) { // NOPMD as this is implementing the abstract class
    super.setUseShortClassName(useShortClassName);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use the identity hash code.
   *
   * @return the current useIdentityHashCode flag
   */
  @Override
  public boolean isUseIdentityHashCode() { // NOPMD as this is implementing the abstract class
    return super.isUseIdentityHashCode();
  }

  /**
   * Sets whether to use the identity hash code.
   *
   * @param useIdentityHashCode the new useIdentityHashCode flag
   */
  @Override
  public void setUseIdentityHashCode(
      final boolean useIdentityHashCode) { // NOPMD as this is implementing the abstract class
    super.setUseIdentityHashCode(useIdentityHashCode);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use the field names passed in.
   *
   * @return the current useFieldNames flag
   */
  @Override
  public boolean isUseFieldNames() { // NOPMD as this is implementing the abstract class
    return super.isUseFieldNames();
  }

  /**
   * Sets whether to use the field names passed in.
   *
   * @param useFieldNames the new useFieldNames flag
   */
  @Override
  public void setUseFieldNames(
      final boolean useFieldNames) { // NOPMD as this is implementing the abstract class
    super.setUseFieldNames(useFieldNames);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to use full detail when the caller doesn't specify.
   *
   * @return the current defaultFullDetail flag
   */
  @Override
  public boolean isDefaultFullDetail() { // NOPMD as this is implementing the abstract class
    return super.isDefaultFullDetail();
  }

  /**
   * Sets whether to use full detail when the caller doesn't specify.
   *
   * @param defaultFullDetail the new defaultFullDetail flag
   */
  @Override
  public void setDefaultFullDetail(
      final boolean defaultFullDetail) { // NOPMD as this is implementing the abstract class
    super.setDefaultFullDetail(defaultFullDetail);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether to output array content detail.
   *
   * @return the current array content detail setting
   */
  @Override
  public boolean isArrayContentDetail() { // NOPMD as this is implementing the abstract class
    return super.isArrayContentDetail();
  }

  /**
   * Sets whether to output array content detail.
   *
   * @param arrayContentDetail the new arrayContentDetail flag
   */
  @Override
  public void setArrayContentDetail(
      final boolean arrayContentDetail) { // NOPMD as this is implementing the abstract class
    super.setArrayContentDetail(arrayContentDetail);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the array start text.
   *
   * @return the current array start text
   */
  @Override
  public String getArrayStart() { // NOPMD as this is implementing the abstract class
    return super.getArrayStart();
  }

  /**
   * Sets the array start text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param arrayStart the new array start text
   */
  @Override
  public void setArrayStart(
      final String arrayStart) { // NOPMD as this is implementing the abstract class
    super.setArrayStart(arrayStart);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the array end text.
   *
   * @return the current array end text
   */
  @Override
  public String getArrayEnd() { // NOPMD as this is implementing the abstract class
    return super.getArrayEnd();
  }

  /**
   * Sets the array end text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param arrayEnd the new array end text
   */
  @Override
  public void setArrayEnd(
      final String arrayEnd) { // NOPMD as this is implementing the abstract class
    super.setArrayEnd(arrayEnd);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the array separator text.
   *
   * @return the current array separator text
   */
  @Override
  public String getArraySeparator() { // NOPMD as this is implementing the abstract class
    return super.getArraySeparator();
  }

  /**
   * Sets the array separator text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param arraySeparator the new array separator text
   */
  @Override
  public void setArraySeparator(
      final String arraySeparator) { // NOPMD as this is implementing the abstract class
    super.setArraySeparator(arraySeparator);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the content start text.
   *
   * @return the current content start text
   */
  @Override
  public String getContentStart() { // NOPMD as this is implementing the abstract class
    return super.getContentStart();
  }

  /**
   * Sets the content start text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param contentStart the new content start text
   */
  @Override
  public void setContentStart(
      final String contentStart) { // NOPMD as this is implementing the abstract class
    super.setContentStart(contentStart);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the content end text.
   *
   * @return the current content end text
   */
  @Override
  public String getContentEnd() { // NOPMD as this is implementing the abstract class
    return super.getContentEnd();
  }

  /**
   * Sets the content end text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param contentEnd the new content end text
   */
  @Override
  public void setContentEnd(
      final String contentEnd) { // NOPMD as this is implementing the abstract class
    super.setContentEnd(contentEnd);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the field name value separator text.
   *
   * @return the current field name value separator text
   */
  @Override
  public String getFieldNameValueSeparator() { // NOPMD as this is implementing the abstract class
    return super.getFieldNameValueSeparator();
  }

  /**
   * Sets the field name value separator text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param fieldNameValueSeparator the new field name value separator text
   */
  @Override
  public void setFieldNameValueSeparator(
      final String fieldNameValueSeparator) { // NOPMD as this is implementing the abstract class
    super.setFieldNameValueSeparator(fieldNameValueSeparator);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the field separator text.
   *
   * @return the current field separator text
   */
  @Override
  public String getFieldSeparator() { // NOPMD as this is implementing the abstract class
    return super.getFieldSeparator();
  }

  /**
   * Sets the field separator text.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param fieldSeparator the new field separator text
   */
  @Override
  public void setFieldSeparator(
      final String fieldSeparator) { // NOPMD as this is implementing the abstract class
    super.setFieldSeparator(fieldSeparator);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether the field separator should be added at the start of each buffer.
   *
   * @return the fieldSeparatorAtStart flag
   * @since 2.0
   */
  @Override
  public boolean isFieldSeparatorAtStart() { // NOPMD as this is implementing the abstract class
    return super.isFieldSeparatorAtStart();
  }

  /**
   * Sets whether the field separator should be added at the start of each buffer.
   *
   * @param fieldSeparatorAtStart the fieldSeparatorAtStart flag
   * @since 2.0
   */
  @Override
  public void setFieldSeparatorAtStart(
      final boolean fieldSeparatorAtStart) { // NOPMD as this is implementing the abstract class
    super.setFieldSeparatorAtStart(fieldSeparatorAtStart);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets whether the field separator should be added at the end of each buffer.
   *
   * @return fieldSeparatorAtEnd flag
   * @since 2.0
   */
  @Override
  public boolean isFieldSeparatorAtEnd() { // NOPMD as this is implementing the abstract class
    return super.isFieldSeparatorAtEnd();
  }

  /**
   * Sets whether the field separator should be added at the end of each buffer.
   *
   * @param fieldSeparatorAtEnd the fieldSeparatorAtEnd flag
   * @since 2.0
   */
  @Override
  public void setFieldSeparatorAtEnd(
      final boolean fieldSeparatorAtEnd) { // NOPMD as this is implementing the abstract class
    super.setFieldSeparatorAtEnd(fieldSeparatorAtEnd);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the text to output when <code>null</code> found.
   *
   * @return the current text to output when <code>null</code> found
   */
  @Override
  public String getNullText() { // NOPMD as this is implementing the abstract class
    return super.getNullText();
  }

  /**
   * Sets the text to output when <code>null</code> found.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param nullText the new text to output when <code>null</code> found
   */
  @Override
  public void setNullText(
      final String nullText) { // NOPMD as this is implementing the abstract class
    super.setNullText(nullText);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the text to output when a <code>Collection</code>, <code>Map</code> or <code>Array</code>
   * size is output.
   *
   * <p>This is output before the size value.
   *
   * @return the current start of size text
   */
  @Override
  public String getSizeStartText() { // NOPMD as this is implementing the abstract class
    return super.getSizeStartText();
  }

  /**
   * Sets the start text to output when a <code>Collection</code>, <code>Map</code> or <code>Array
   * </code> size is output.
   *
   * <p>This is output before the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param sizeStartText the new start of size text
   */
  @Override
  public void setSizeStartText(
      final String sizeStartText) { // NOPMD as this is implementing the abstract class
    super.setSizeStartText(sizeStartText);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the end text to output when a <code>Collection</code>, <code>Map</code> or <code>Array
   * </code> size is output.
   *
   * <p>This is output after the size value.
   *
   * @return the current end of size text
   */
  @Override
  public String getSizeEndText() { // NOPMD as this is implementing the abstract class
    return super.getSizeEndText();
  }

  /**
   * Sets the end text to output when a <code>Collection</code>, <code>Map</code> or <code>Array
   * </code> size is output.
   *
   * <p>This is output after the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param sizeEndText the new end of size text
   */
  @Override
  public void setSizeEndText(
      final String sizeEndText) { // NOPMD as this is implementing the abstract class
    super.setSizeEndText(sizeEndText);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the start text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output before the size value.
   *
   * @return the current start of summary text
   */
  @Override
  public String getSummaryObjectStartText() { // NOPMD as this is implementing the abstract class
    return super.getSummaryObjectStartText();
  }

  /**
   * Sets the start text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output before the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param summaryObjectStartText the new start of summary text
   */
  @Override
  public void setSummaryObjectStartText(
      final String summaryObjectStartText) { // NOPMD as this is implementing the abstract class
    super.setSummaryObjectStartText(summaryObjectStartText);
  }

  // ---------------------------------------------------------------------

  /**
   * Gets the end text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output after the size value.
   *
   * @return the current end of summary text
   */
  @Override
  public String getSummaryObjectEndText() { // NOPMD as this is implementing the abstract class
    return super.getSummaryObjectEndText();
  }

  /**
   * Sets the end text to output when an <code>Object</code> is output in summary mode.
   *
   * <p>This is output after the size value.
   *
   * <p><code>null</code> is accepted, but will be converted to an empty String.
   *
   * @param summaryObjectEndText the new end of summary text
   */
  @Override
  public void setSummaryObjectEndText(
      final String summaryObjectEndText) { // NOPMD as this is implementing the abstract class
    super.setSummaryObjectEndText(summaryObjectEndText);
  }

  // ---------------------------------------------------------------------

}
