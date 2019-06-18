package br.com.jbd.user.info.service;

import br.com.jbd.user.info.dto.Address;
import br.com.jbd.user.info.dto.UserData;
import br.com.jbd.user.info.model.User;
import br.com.jbd.user.info.model.UserAddress;
import br.com.jbd.user.info.repository.UserDataBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserService {

    @Autowired
    private UserDataBaseRepository userDataBaseRepository;

    public Optional<UserData> findUser(Long id) {
        // TODO - Adicionar MongoDB e implementar lógica para "popular" os dados do usuário no MongoDB
        return userDataBaseRepository.findById(id).map(this::convertDataFromUserModel);
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
