package com.databases.shop.services.implementations;

import com.databases.shop.exceptions.salesman.NoSalesmanWithSuchIdException;
import com.databases.shop.exceptions.salesman.SalesmanRegistrationException;
import com.databases.shop.exceptions.salesman.SalesmanWithEmailAlreadyExistsException;
import com.databases.shop.mapstruct.dtos.dataDtos.SalesmanFilterBoundsDto;
import com.databases.shop.mapstruct.dtos.salesman.SalesmanGetDto;
import com.databases.shop.mapstruct.dtos.salesman.SalesmanPostDto;
import com.databases.shop.mapstruct.mappers.SalesmanMapper;
import com.databases.shop.models.Salesman;
import com.databases.shop.repositories.CustomerRepository;
import com.databases.shop.repositories.SalesmanFilterRepository;
import com.databases.shop.repositories.SalesmanRepository;
import com.databases.shop.repositories.queryinterfaces.MaxOrderCount;
import com.databases.shop.repositories.queryinterfaces.MaxSalesmanIncome;
import com.databases.shop.services.interfaces.AdminService;
import com.databases.shop.services.interfaces.SalesmanService;
import com.databases.shop.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SalesmanServiceImpl implements SalesmanService {


    @Autowired
    private SalesmanRepository salesmanRepository;

    @Autowired
    private SalesmanFilterRepository salesmanFilterRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private Utils utils;

    @Autowired
    private SalesmanMapper salesmanMapper;

    @Autowired
    private AdminService adminService;

    @Override
    public Iterable<Salesman> findAll() {
        return salesmanRepository.getAll();
    }

    @Override
    public Salesman findById(Long id) {
        return null;
    }

    @Override
    public Salesman save(Salesman salesman) {
        utils.processSalesman(salesman);
        utils.checkPersonName(salesman.getPersonName());
        utils.checkContacts(salesman.getContacts());

        return salesmanRepository.save(salesman);
    }

//    @Override
//    public Salesman save(Salesman salesman) {
//        utils.processSalesman(salesman);
//        utils.checkPersonName(salesman.getPersonName());
//        utils.checkContacts(salesman.getContacts());
//        if (usersWithEmailExist(salesman.getContacts().getEmail())) {
//            throw new SalesmanWithEmailAlreadyExistsException(salesman.getContacts().getEmail());
//        }
//        return salesmanRepository.save(salesman);
//    }

    public SalesmanGetDto saveSalesmanPostDto(SalesmanPostDto salesmanPostDto) {
        try {
            adminService.registerUser(salesmanPostDto.getContacts().getEmail(),salesmanPostDto.getPassword());
            adminService.saveUserToFirestore(salesmanPostDto.getContacts().getEmail(),salesmanPostDto.getRole());
            return salesmanMapper.salesmanToSalesmanGetDto(
                    save(salesmanMapper.salesmanSaveDtoToSalesman(
                                    salesmanMapper.salesmanPostDtoToSalesmanSaveDto(salesmanPostDto))));

        } catch (Exception e) {
            throw new SalesmanRegistrationException();
        }
    }

    @Override
    public SalesmanFilterBoundsDto getSalesmanFilterBounds() {
        MaxOrderCount maxOrderCount = salesmanRepository.maxOrderCount();
        MaxSalesmanIncome maxSalesmanIncome = salesmanRepository.maxSalesmanIncome();

        SalesmanFilterBoundsDto salesmanFilterBoundsDto = new SalesmanFilterBoundsDto();

        salesmanFilterBoundsDto.setMaxOrderCount(maxOrderCount.getMaxCount());

        salesmanFilterBoundsDto.setMaxIncome(maxSalesmanIncome.getMaxIncome());

        return salesmanFilterBoundsDto;
    }

    @Override
    public Iterable<Salesman> getFilteredSalesmen(double income, int orders, boolean hasAllCategories) {
        return salesmanFilterRepository.filterSalesmen(income,orders,hasAllCategories);
    }

    @Override
    public Salesman update(Long id, Salesman salesman) {
        salesman.setPersonName(utils.processPersonName(salesman.getPersonName()));
        utils.checkPersonName(salesman.getPersonName());
        utils.checkPhoneNumber(salesman.getContacts().getPhoneNumber());

        Salesman s = salesmanRepository.findById(id).orElseThrow(() -> new NoSalesmanWithSuchIdException(id));
        s.setPersonName(salesman.getPersonName());
        s.getContacts().setPhoneNumber(salesman.getContacts().getPhoneNumber());
        return salesmanRepository.save(s);
    }

//    @Override
//    public boolean usersWithEmailExist(String email) {
//        return salesmanRepository.existsByEmail(email) ||
//                customerRepository.existsByEmail(email);
//    }

    @Override
    public boolean delete(Long id) {
        if (salesmanRepository.existsById(id)) {
            Salesman s = salesmanRepository.getById(id);
            try {
                adminService.deleteUserAccountByEmail(s.getContacts().getEmail());
                adminService.deleteUserFromFirestore(s.getContacts().getEmail());
                salesmanRepository.deleteById(id);
                return true;
            } catch (Exception e) {
                return false;
            }
        };
        return false;

    }


}
