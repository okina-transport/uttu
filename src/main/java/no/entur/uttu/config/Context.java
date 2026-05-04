/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.entur.uttu.config;

import no.entur.uttu.util.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;


public class Context {

    private static final ThreadLocal<String> providerPerThread = new ThreadLocal<>();
    private static final ThreadLocal<String> usernamePerThread = new ThreadLocal<>();

    private Context() {
    }

    public static String getProvider() {
        return providerPerThread.get();
    }

    public static void setProvider(String providerCode) {
        Preconditions.checkArgument(providerCode != null,
                "Attempt to set providerCode = null for session");
        providerPerThread.set(providerCode);
    }

    public static void clear() {
        providerPerThread.remove();
        usernamePerThread.remove();
    }

    public static String getUsername() {
        if (StringUtils.isNotBlank(usernamePerThread.get())) {
            return usernamePerThread.get();
        }
        String user = "unknown";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            user = Objects.toString(auth.getPrincipal());
        }
        return user;
    }

    public static void setUsername(String username) {
        Preconditions.checkArgument(username != null, "Attempt to set username = null for session");
        usernamePerThread.set(username);
    }

    public static String getVerifiedProviderCode() {
        String providerCode = Context.getProvider();
        Preconditions.checkArgument(providerCode != null,
                "Provider not set for session");
        return providerCode;
    }
}
