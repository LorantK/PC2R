/*
 * OCaml-SFML - OCaml bindings for the SFML library.
 * Copyright (C) 2012 Florent Monnier <monnier.florent(_)gmail.com>
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 * 1. The origin of this software must not be misrepresented;
 *    you must not claim that you wrote the original software.
 *    If you use this software in a product, an acknowledgment
 *    in the product documentation would be appreciated but is not required.
 *
 * 2. Altered source versions must be plainly marked as such,
 *    and must not be misrepresented as being the original software.
 *
 * 3. This notice may not be removed or altered from any source distribution.
 */

#include <SFML/Graphics/Font.hpp>

#include "sf_caml_incs.hpp"
#include "SFFont_stub.hpp"
#include "SFRect_stub.hpp"

/* sf::Font */

CAMLextern_C value
caml_sfFont_createFromFile(value filename)
{
    sf::Font *font = new sf::Font;
    bool loaded = font->loadFromFile(String_val(filename));
    if (!loaded) {
        delete font;
        caml_failwith("SFFont.createFromFile");
    }
    return Val_sfFont(font);
}

CAMLextern_C value
caml_sfFont_createFromMemory(value data)
{
    sf::Font *font = new sf::Font;
    bool loaded = font->loadFromMemory(String_val(data), caml_string_length(data));
    if (!loaded) {
        delete font;
        caml_failwith("SFFont.createFromMemory");
    }
    return Val_sfFont(font);
}

CAMLextern_C value
caml_sfFont_destroy(value font)
{
    delete SfFont_val(font);
#if defined(_OCAML_SFML_DEBUG)
    std::cout << "# font deleted" << std::endl << std::flush;  // XXX: DEBUG
#endif
    return Val_unit;
}

CAMLextern_C value
caml_sfFont_copy(value font)
{
    return Val_sfFont(new sf::Font(*SfFont_val(font)));
}

CAMLextern_C value
caml_sfFont_getGlyph(value font, value codePoint, value characterSize, value bold)
{
    CAMLparam4(font, codePoint, characterSize, bold);
    CAMLlocal1(ret);
    sf::Glyph glyph = SfFont_val(font)->getGlyph(
        Int32_val(codePoint), Long_val(characterSize), Bool_val(bold));
    ret = caml_alloc(3, 0);
    Store_field(ret, 0, Val_long(glyph.advance));
    Store_field(ret, 1, caml_copy_sfIntRect(glyph.bounds));
    Store_field(ret, 2, caml_copy_sfIntRect(glyph.textureRect));
    CAMLreturn(ret);
}

CAMLextern_C value
caml_sfFont_getKerning(value font, value first, value second, value characterSize)
{
    int k = SfFont_val(font)->getKerning(
        Int32_val(first), Int32_val(second), Long_val(characterSize));
    return Val_int(k);
}

CAMLextern_C value
caml_sfFont_getLineSpacing(value font, value characterSize)
{
    int s = SfFont_val(font)->getLineSpacing(Long_val(characterSize));
    return Val_int(s);
}

/* TODO
sfFont* sfFont_createFromStream(sfInputStream* stream);
const sfTexture* sfFont_getTexture(sfFont* font, unsigned int characterSize);
*/

// vim: sw=4 sts=4 ts=4 et
