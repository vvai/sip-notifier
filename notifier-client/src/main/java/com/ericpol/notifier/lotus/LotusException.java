// This source code is released under the GPL v3 license, http://www.gnu.org/licenses/gpl.html.
// This file is part of the LNGS project: http://sourceforge.net/projects/lngooglecalsync.
package com.ericpol.notifier.lotus;

// It is poor design to throw base Exception objects so use this derived class.
public class LotusException extends Exception
{
    public LotusException(final String aMessage)
    {
        super(aMessage);
    }

    public LotusException(final String aMessage, final Throwable aCause)
    {
        super(aMessage, aCause);
    }
}