package org.nextprot.api.user.aop;

import org.nextprot.api.commons.exception.NotAuthorizedException;
import org.nextprot.api.commons.resource.UserResource;
import org.nextprot.api.user.dao.UserQueryDao;
import org.nextprot.api.user.domain.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserQueryUserResourceAuthorizationChecker implements UserResourceAuthorizationChecker {

    @Autowired
    private UserQueryDao dao;

    @Override
    public void checkAuthorization(UserResource query) {

        if (query instanceof UserQuery) {

            long queryId = ((UserQuery) query).getUserQueryId();

            if (((UserQuery) query).getUserQueryId() != 0){

                UserQuery foundUserQuery = dao.getUserQueryById(queryId);

                // dao only get owner name
                if (!foundUserQuery.getOwner().equals(query.getOwnerName()))
                    throw new NotAuthorizedException(query.getOwnerName() + " cannot access resource");
            }
        }
        else {

            throw new IllegalStateException(query.getClass().getSimpleName() + ": incorrect class for authorization check");
        }
    }

    @Override
    public boolean supports(UserResource ur) {

        return ur instanceof UserQuery;
    }
}
