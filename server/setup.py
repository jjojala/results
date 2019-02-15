import setuptools

setuptools.setup(
    name="tupal-server",
    version="0.0.1",
    description="TUPAL backend",
    scripts=['server.py'],
    packages=setuptools.find_packages(),
    install_requires = [
        'Flask>=1,<2',
        'Flask-RESTful>=0.3.7,<1',
        'Flask-SocketIO>=3.2.2,<4'
    ],
    classifiers= [
        "Programming Language :: Python :: 3"
    ]
)
