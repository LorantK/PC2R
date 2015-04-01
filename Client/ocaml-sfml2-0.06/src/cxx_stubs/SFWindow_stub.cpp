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

#include <SFML/Window.hpp>

#include "sf_caml_incs.hpp"

#include "sf_caml_conv.hpp"
#include "sf_conv_vectors.hpp"
#include "sf_context_settings.hpp"

#include "SFWindow_stub.hpp"
#include "SFEvent_stub.hpp"

#define Val_sfWindow(win) ((value)(win))
#define SfWindow_val(win) ((sf::Window *)(win))


/* sf::Window */

CAMLextern_C value
caml_sfWindow_create(
    value ml_mode, value title, value ml_style, value ml_settings)
{
    sf::ContextSettings settings;

    SfContextSettings_val(&settings, ml_settings);

    sf::VideoMode mode;

    mode.width        = Long_val(Field(ml_mode, 0));
    mode.height       = Long_val(Field(ml_mode, 1));
    mode.bitsPerPixel = Long_val(Field(ml_mode, 2));

    sf::Uint32 style = sf::Style::None;
    while (ml_style != Val_emptylist)
    {
        switch (Long_val(Field(ml_style, 0)))
        {
            case SF_WIN_STYLE_TITLEBAR:   style |= sf::Style::Titlebar;   break;
            case SF_WIN_STYLE_RESIZE:     style |= sf::Style::Resize;     break;
            case SF_WIN_STYLE_CLOSE:      style |= sf::Style::Close;      break;
            case SF_WIN_STYLE_FULLSCREEN: style |= sf::Style::Fullscreen; break;
            case SF_WIN_STYLE_DEFAULT:    style |= sf::Style::Default;    break;
            default: caml_failwith("SFWindow.create");
        }
        ml_style = Field(ml_style, 1);
    }

    if (style & sf::Style::Fullscreen && !mode.isValid())
        caml_invalid_argument("SFWindow.create: video_mode");

    sf::Window *window;
    window = new sf::Window;
    window->create(mode, String_val(title), style, settings);

    return Val_sfWindow(window);
}

CAMLextern_C value
caml_sfWindow_createFromHandle(value ml_handle, value ml_settings)
{
    sf::ContextSettings settings;

    SfContextSettings_val(&settings, ml_settings);

    sf::Window *window;
    window = new sf::Window;
    window->create(Nativeint_val(ml_handle), settings);

    return Val_sfWindow(window);
}

CAMLextern_C value
caml_sfWindow_destroy(value win)
{
    delete SfWindow_val(win);
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_isOpen(value win)
{
    return Val_bool(
        SfWindow_val(win)->isOpen());
}

CAMLextern_C value
caml_sfWindow_close(value win)
{
    SfWindow_val(win)->close();
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_display(value win)
{
    caml_enter_blocking_section();
    SfWindow_val(win)->display();
    caml_leave_blocking_section();
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_pollEvent(value win)
{
    sf::Event event;
    const char func_name[] = "SFWindow.pollEvent";
    bool isev = SfWindow_val(win)->pollEvent(event);
    if (isev) return Val_some(Val_sfEvent(event, func_name));
    else return Val_none;
}

CAMLextern_C value
caml_sfWindow_waitEvent(value win)
{
    sf::Event event;
    const char func_name[] = "SFWindow.waitEvent";
    bool err = SfWindow_val(win)->waitEvent(event);
    if (err) caml_failwith(func_name);
    return Val_sfEvent(event, func_name);
}

CAMLextern_C value
caml_sfWindow_getSize(value win)
{
    sf::Vector2u size = SfWindow_val(win)->getSize();
    return Val_sfVector2u(size);
}

CAMLextern_C value
caml_sfWindow_getWidth(value win)
{
    sf::Vector2u size = SfWindow_val(win)->getSize();
    return Val_long(size.x);
}

CAMLextern_C value
caml_sfWindow_getHeight(value win)
{
    sf::Vector2u size = SfWindow_val(win)->getSize();
    return Val_long(size.y);
}

CAMLextern_C value
caml_sfWindow_setSize2(value win, value width, value height)
{
    SfWindow_val(win)->setSize(SfVector2u_val2(width, height));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setSize(value win, value size)
{
    SfWindow_val(win)->setSize(SfVector2u_val(size));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setPosition2(value win, value left, value top)
{
    SfWindow_val(win)->setPosition(SfVector2i_val2(left, top));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setPosition(value win, value pos)
{
    SfWindow_val(win)->setPosition(SfVector2i_val(pos));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_getPosition(value win)
{
    sf::Vector2i pos = SfWindow_val(win)->getPosition();
    return Val_sfVector2i(pos);
}

CAMLextern_C value
caml_sfWindow_setTitle(value win, value title)
{
    SfWindow_val(win)->setTitle(String_val(title));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setActive(value win, value active)
{
    bool a = SfWindow_val(win)->setActive(Bool_val(active));
    return Val_bool(a);
}

CAMLextern_C value
caml_sfWindow_setVisible(value win, value visible)
{
    SfWindow_val(win)->setVisible(Bool_val(visible));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setMouseCursorVisible(value win, value visible)
{
    SfWindow_val(win)->setMouseCursorVisible(Bool_val(visible));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setKeyRepeatEnabled(value win, value enabled)
{
    SfWindow_val(win)->setKeyRepeatEnabled(Bool_val(enabled));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setJoystickThreshold(value win, value threshold)
{
    SfWindow_val(win)->setJoystickThreshold(Double_val(threshold));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setFramerateLimit(value win, value limit)
{
    SfWindow_val(win)->setFramerateLimit(Long_val(limit));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_setVerticalSyncEnabled(value win, value enabled)
{
    SfWindow_val(win)->setVerticalSyncEnabled(Bool_val(enabled));
    return Val_unit;
}

CAMLextern_C value
caml_sfWindow_getSettings(value win)
{
    sf::ContextSettings settings = SfWindow_val(win)->getSettings();
    return Val_sfContextSettings(&settings);
}

CAMLextern_C value
caml_sfWindow_getMousePosition(value relativeTo)
{
    sf::Vector2i pos = sf::Mouse::getPosition(*SfWindow_val(relativeTo));
    return Val_sfVector2i(pos);
}

CAMLextern_C value
caml_sfWindow_setMousePosition(value relativeTo, value position)
{
    sf::Mouse::setPosition(SfVector2i_val(position), *SfWindow_val(relativeTo));
    return Val_unit;
}

/* TODO
void sfWindow_setSize(sfWindow* window, sfVector2u size);

void sfWindow_setIcon(sfWindow* window,
        unsigned int width, unsigned int height, const sfUint8* pixels);

sfWindowHandle sfWindow_getSystemHandle(const sfWindow* window);
*/

// vim: sw=4 sts=4 ts=4 et
