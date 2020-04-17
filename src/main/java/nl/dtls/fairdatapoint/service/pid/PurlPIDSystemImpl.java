/**
 * The MIT License
 * Copyright © 2017 DTL
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.dtls.fairdatapoint.service.pid;

import com.google.common.base.Preconditions;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import static nl.dtls.fairdatapoint.util.ValueFactoryHelper.i;

/**
 * Implementation of purl.org PID system
 */
public class PurlPIDSystemImpl implements PIDSystem {

    private String instanceUrl;

    private IRI purlBaseUrl;

    public PurlPIDSystemImpl(@Value("${instance.url}") String instanceUrl, @Qualifier("purlBaseUrl") IRI purlBaseUrl) {
        this.instanceUrl = instanceUrl;
        this.purlBaseUrl = purlBaseUrl;
    }

    /**
     * Create a new purl.org PID uri for a given metadata
     *
     * @return PID uri as IRI
     */
    @Override
    public IRI getURI(IRI uri) throws IllegalStateException {
        return i(uri.stringValue().replace(instanceUrl, purlBaseUrl.stringValue()));
    }

    /**
     * Returns identifier of a given purl PID iri.
     *
     * @param iri Purl PID IRI
     * @return ID as String
     * @throws NullPointerException  exception if the purl pid URI is null
     * @throws IllegalStateException exception if the purl URI doesn't contain "purl.org" string
     */
    @Override
    public String getId(IRI iri) {
        Preconditions.checkNotNull(iri, "Purl pid uri must not be null.");
        Preconditions.checkState(iri.toString().contains("purl.org"), "Not an valid default pid uri.");
        return iri.getLocalName();
    }

}
