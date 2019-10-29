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
package nl.dtls.fairdatapoint.acceptance.user;

import nl.dtls.fairdatapoint.WebIntegrationTest;
import nl.dtls.fairdatapoint.api.dto.error.ErrorDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserChangeDTO;
import nl.dtls.fairdatapoint.api.dto.user.UserDTO;
import nl.dtls.fairdatapoint.database.mongo.fixtures.UserFixtures;
import nl.dtls.fairdatapoint.entity.user.User;
import nl.dtls.fairdatapoint.entity.user.UserRole;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static java.lang.String.format;
import static nl.dtls.fairdatapoint.acceptance.Common.createForbiddenTestPut;
import static nl.dtls.fairdatapoint.acceptance.Common.createNotFoundTestPut;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

public class User_Detail_PUT extends WebIntegrationTest {

    private URI url(String uuid) {
        return URI.create(format("/users/%s", uuid));
    }

    private UserChangeDTO reqDto() {
        return new UserChangeDTO("EDITED: Albert", "EDITED: Einstein", "albert.einstein.edited@example.com",
                UserRole.USER);
    }

    @Autowired
    private UserFixtures userFixtures;

    @Test
    public void res200() {
        // GIVEN:
        User user = userFixtures.albert();
        RequestEntity<UserChangeDTO> request = RequestEntity
                .put(url(user.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .body(reqDto());
        ParameterizedTypeReference<UserDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<UserDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.OK)));
        Common.compare(reqDto(), result.getBody());
    }

    @Test
    public void res400_emailAlreadyExists() {
        // GIVEN:
        User user = userFixtures.albert();
        UserChangeDTO reqDto = new UserChangeDTO("EDITED: Albert", "EDITED: Einstein", "nikola.tesla@example.com",
                UserRole.USER);
        RequestEntity<UserChangeDTO> request = RequestEntity
                .put(url(user.getUuid()))
                .header(HttpHeaders.AUTHORIZATION, TOKEN)
                .body(reqDto);
        ParameterizedTypeReference<ErrorDTO> responseType = new ParameterizedTypeReference<>() {
        };

        // WHEN:
        ResponseEntity<ErrorDTO> result = client.exchange(request, responseType);

        // THEN:
        assertThat(result.getStatusCode(), is(equalTo(HttpStatus.BAD_REQUEST)));
        assertThat(result.getBody().getMessage(), is(format("Email '%s' is already taken", reqDto.getEmail())));
    }

    @Test
    public void res403() {
        User user = userFixtures.albert();
        createForbiddenTestPut(client, url(user.getUuid()), reqDto());
    }

    @Test
    public void res404() {
        createNotFoundTestPut(client, url("nonExisting"), reqDto());
    }

}
