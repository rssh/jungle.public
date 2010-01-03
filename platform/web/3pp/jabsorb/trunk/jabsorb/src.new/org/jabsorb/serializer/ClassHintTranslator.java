package org.jabsorb.serializer;

/**
 * ClassHint translator.
 *   The work of translator is to substitute classes,
 *   when we want different classes on server and client side
 *   for the same beans.
 *   For example - we want internal hibernate POJO extensions
 *   be translated on client side to plain POJO object.
 *   Or, from other side, we want pass from client callable references
 *   to interfaces, not implementation objects.
 **/
public interface ClassHintTranslator 
{
  /**
   * translate class name or return null if translation is not applicable.
   **/
  public Class translate(Class clazz);
}

