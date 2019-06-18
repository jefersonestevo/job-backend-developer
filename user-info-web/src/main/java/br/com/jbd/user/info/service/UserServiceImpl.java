package br.com.jbd.user.info.service;

import br.com.jbd.user.info.dto.Address;
import br.com.jbd.user.info.dto.UserData;
import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.model.UserAddress;
import br.com.jbd.user.info.repository.UserDataBaseRepository;
import br.com.jbd.user.info.repository.UserDataMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDataBaseRepository userDataBaseRepository;

    @Autowired
    private UserDataMongoRepository userDataMongoRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    public Optional<UserData> findUser(Long id) {
        Optional<UserData> userData = userDataMongoRepository.findById(id);
        if (!userData.isPresent()) {
            userData = this.populateUserData(id);
        }
        return userData;
    }

    private Optional<UserData> populateUserData(Long id) {
        Optional<UserData> userData = userDataBaseRepository.findById(id).map(this::convertDataFromUserModel);

        if (userData.isPresent()) {
            userDataMongoRepository.save(userData.get());

            try {
                transactionTemplate.execute((status) -> {
                    userDataBaseRepository.registerImport(id);
                    return null;
                });
            } catch (DuplicateKeyException e) {
                LOG.warn(String.format("Error trying to register import for user %d", id), e);
            }
        }
        return userData;
    }

    private UserData convertDataFromUserModel(User user) {
        UserData userData = new UserData();
        userData.setId(user.getId());
        userData.setLogin(user.getLogin());
        userData.setName(user.getUserInfo().getName());
        userData.setLastName(user.getUserInfo().getLastName());
        userData.setEmail(user.getUserInfo().getEmail());
        userData.setPhoneNumber(user.getUserInfo().getPhoneNumber());

        List<Address> addresses = new ArrayList<>(user.getAddresses().size());
        for (UserAddress userAddress : user.getAddresses()) {
            Address address = new Address();
            address.setStreet(userAddress.getStreet());
            address.setNumber(userAddress.getNumber());
            address.setCity(userAddress.getCity());
            address.setState(userAddress.getState());
            address.setCountry(userAddress.getCountry());

            addresses.add(address);
        }
        userData.setAddresses(addresses);

        return userData;
    }

}
